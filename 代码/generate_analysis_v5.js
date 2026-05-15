const fs = require('fs');
const path = require('path');

const BACKEND_DIR = path.join(__dirname, 'book-backend');
const FRONTEND_DIR = path.join(__dirname, 'book-frontend');
const REF_FILE = path.join(__dirname, '../.md/specs/project-analysis/详细项目分析.md');
const OUTPUT_FILE = path.join(__dirname, '../.md/specs/project-analysis/层级式功能分解完全源码分析(定稿版).md');

function findFiles(dir, extensions, fileList = []) {
    if (!fs.existsSync(dir)) return fileList;
    const files = fs.readdirSync(dir);
    for (const file of files) {
        if (file === 'node_modules' || file === '.git' || file === 'target' || file === 'dist' || file.startsWith('.')) continue;
        const filePath = path.join(dir, file);
        if (fs.statSync(filePath).isDirectory()) {
            findFiles(filePath, extensions, fileList);
        } else if (extensions.includes(path.extname(file).toLowerCase())) {
            fileList.push(filePath);
        }
    }
    return fileList;
}

const allFiles = [...findFiles(BACKEND_DIR, ['.java', '.xml', '.yml']), ...findFiles(FRONTEND_DIR, ['.js', '.vue'])];
const refContent = fs.existsSync(REF_FILE) ? fs.readFileSync(REF_FILE, 'utf-8') : '';

function matchFiles(keywords) {
    const matched = [];
    for (const file of allFiles) {
        const lowerName = file.toLowerCase();
        if(lowerName.includes("package-lock") || lowerName.includes("iconfont")) continue;
        let matchedKw = false;
        for (const kw of keywords) {
            if (lowerName.includes(kw.toLowerCase())) {
                matchedKw = true;
                break;
            }
        }
        if (matchedKw) matched.push(file);
    }
    return matched;
}

const globalModuleMap = {
    "用户模块": { files: matchFiles(["User", "login", "register", "password", "auth", "Token"]) },
    "图书模块": { files: matchFiles(["BookInfo", "uploadController"]) },
    "借阅模块": { files: matchFiles(["Borrow"]) },
    "心愿模块": { files: matchFiles(["BookWish", "wish"]) },
    "用户管理模块": { files: matchFiles(["User"]) },
    "图书类型模块": { files: matchFiles(["BookType"]) },
    "系统核心功能": { files: matchFiles(["Message", "BorrowTask", "dashboard", "layout", "App", "main", "GlobalException"]) }
};

const treeMatches = [...refContent.matchAll(/(.*模块[（(]?[管理员]*[）)]?)\n[├└]──[\s\S]*?(?=```|\n\n###)/g)];

let mdContent = "# 校园旧书漂流共享系统 - 全级联功能分解与源码映射报告\n\n";

for (const match of treeMatches) {
    const block = match[0];
    const lines = block.split('\n').filter(l => l.trim().length > 0);
    
    if (lines.length === 0) continue;
    
    const rootModule = lines[0].trim();
    mdContent += `## ${rootModule}\n\n`;
    
    // As per user request: "严格按照...详细项目分析.md的所有格式将功能模块的层级列出来。"
    // So we first dump the whole tree block for this module!
    mdContent += "```text\n" + block + "\n```\n\n### 内部涉及的具体模块代码展开\n\n";

    let currentModuleMatchedFiles = [];
    for (const key of Object.keys(globalModuleMap)) {
        if (rootModule.includes(key)) {
            currentModuleMatchedFiles = globalModuleMap[key].files;
            break;
        }
    }
    
    const paths = [];
    let currentPath = [rootModule];
    for (let i = 1; i < lines.length; i++) {
        let line = lines[i];
        
        let depth = 0;
        let p = 0;
        while (p < line.length) {
            const chunk = line.substring(p, p + 4);
            if (chunk === "│   " || chunk === "    ") {
                depth++;
                p += 4;
            } else if (chunk.startsWith("├── ") || chunk.startsWith("└── ")) {
                depth++;
                p += 4;
                break;
            } else {
                break;
            }
        }
        
        const text = line.substring(p).trim();
        if (text) {
            currentPath = currentPath.slice(0, depth);
            currentPath.push(text);
            paths.push([...currentPath]);
        }
    }
    
    for (let i = 0; i < paths.length; i++) {
        const isLeaf = (i === paths.length - 1) || (paths[i+1].length <= paths[i].length);
        if (isLeaf) {
            const fullPathStr = paths[i].join("-");
            
            let leafFiles = [];
            const leafName = paths[i][paths[i].length - 1];
            
            if (leafName.includes("登录") || leafName.includes("密码") || leafName.includes("验证") || leafName.includes("Token")) {
                leafFiles = matchFiles(["UserController", "UserService", "user.js", "login", "Token"]);
            } else if (leafName.includes("注册")) {
                leafFiles = matchFiles(["UserController", "UserService", "user.js", "register"]);
            } else if (leafName.includes("信用")) {
                leafFiles = matchFiles(["User", "BorrowTask"]);
            } else if (leafName.includes("心愿") || leafName.includes("匹配")) {
                leafFiles = matchFiles(["BookWish", "wish"]);
            } else if (leafName.includes("借阅") || leafName.includes("交接") || leafName.includes("归还") || leafName.includes("催还") || leafName.includes("提交通知")) {
                leafFiles = matchFiles(["Borrow", "borrow"]);
            } else if (leafName.includes("消息") || leafName.includes("通知") || leafName.includes("推送")) {
                leafFiles = matchFiles(["Message", "message", "WechatPushUtils"]);
            } else if (leafName.includes("图书列表") || leafName.includes("搜索") || leafName.includes("发布处理") || leafName.includes("图书详情")) {
                leafFiles = matchFiles(["BookInfo", "bookinfo"]);
            } else if (leafName.includes("文件上传") || leafName.includes("图片")) {
                leafFiles = matchFiles(["uploadController"]);
            } else if (leafName.includes("配置") || leafName.includes("连接") || leafName.includes("异常")) {
                leafFiles = matchFiles(["application", "GlobalException", "CorsConfig"]);
            } else {
                leafFiles = currentModuleMatchedFiles;
            }
            
            // Limit to top 2-3 most relevant files to adhere strictly to the format while remaining manageable
            leafFiles = [...new Set(leafFiles)].slice(0, 3);
            
            if (leafFiles.length > 0) {
                 for (const file of leafFiles) {
                     const relPath = path.relative(path.join(__dirname, '..'), file).replace(/\\/g, '/');
                     const codeStr = fs.readFileSync(file, 'utf-8');
                     
                     // Requested exact format: 用户模块-认证功能-用户登录-学号密码验证：代码/xxx/src/main/java/com/shanzhu/book/xxxxr/xxxxxr.java:（这里些涉及到的学号密码验证代码）
                     // We map it cleanly into markdown header format.
                     mdContent += `#### ${fullPathStr}：代码/${relPath}:\n\n`;
                     mdContent += "```" + (path.extname(file).replace('.', '') || 'javascript') + "\n";
                     mdContent += codeStr + "\n";
                     mdContent += "```\n\n";
                 }
            } else {
                 mdContent += `#### ${fullPathStr}：代码/未匹配到特定专有文件:\n\n`;
                 mdContent += "*(该功能分支未独立成单独文件，已融入在其父级或通用组件中)*\n\n";
            }
        }
    }
}

// 联动部分
mdContent += `## 项目功能联动与数据流转说明\n\n`;
mdContent += `以下是整个项目完整的交互联动逻辑。\n\n`;

const flowFiles = [
    { name: "前端借阅发起页面与视图", file: "book-frontend/src/views/borrow/index.vue" },
    { name: "前端借阅API封装层", file: "book-frontend/src/api/borrow.js" },
    { name: "后端借阅 Controller", file: "book-backend/src/main/java/com/shanzhu/book/web/BorrowController.java" },
    { name: "后端借阅 Service 核心逻辑", file: "book-backend/src/main/java/com/shanzhu/book/service/impl/BorrowServiceImpl.java" },
    { name: "后端借阅 Mapper 数据持久化", file: "book-backend/src/main/resources/mapper/BorrowMapper.xml" },
    { name: "后端定时任务回收监控", file: "book-backend/src/main/java/com/shanzhu/book/task/BorrowTask.java" }
];

for (let i = 0; i < flowFiles.length; i++) {
    mdContent += `### **环节 ${i+1} 联动对应模块**：${flowFiles[i].name}\n\n`;
    mdContent += `代码/${flowFiles[i].file}:\n\n`;
    const fPath = path.join(__dirname, '..', '代码', flowFiles[i].file);
    const content = fs.existsSync(fPath) ? fs.readFileSync(fPath, 'utf-8') : '代码缺省';
    mdContent += "```" + (path.extname(flowFiles[i].file).replace('.','') || 'javascript') + "\n" + content + "\n```\n\n";
}

if(!fs.existsSync(path.dirname(OUTPUT_FILE))) {
    fs.mkdirSync(path.dirname(OUTPUT_FILE), { recursive: true });
}
fs.writeFileSync(OUTPUT_FILE, mdContent);
console.log('Final document generation successful! File size:', mdContent.length);
