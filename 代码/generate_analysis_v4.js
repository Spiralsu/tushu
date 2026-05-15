const fs = require('fs');
const path = require('path');

const BACKEND_DIR = path.join(__dirname, 'book-backend');
const FRONTEND_DIR = path.join(__dirname, 'book-frontend');
const REF_FILE = path.join(__dirname, '../.md/specs/project-analysis/详细项目分析.md');
const OUTPUT_FILE = path.join(__dirname, '../.md/specs/project-analysis/层级式功能分解源码级分析.md');

// Utility to find files
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

// Load reference file to parse the tree
const refContent = fs.existsSync(REF_FILE) ? fs.readFileSync(REF_FILE, 'utf-8') : '';

// Function to map a keyword to files
function matchFiles(keywords) {
    const matched = [];
    for (const file of allFiles) {
        const lowerName = file.toLowerCase();
        if(lowerName.includes("package-lock") || lowerName.includes("iconfont")) continue;
        for (const kw of keywords) {
            if (lowerName.includes(kw.toLowerCase())) {
                matched.push(file);
                break;
            }
        }
    }
    return matched;
}

const globalModuleMap = {
    "用户模块": { files: matchFiles(["User", "login", "register", "password", "auth", "Token"]) },
    "图书模块": { files: matchFiles(["BookInfo", "uploadController"]) },
    "借阅模块": { files: matchFiles(["Borrow"]) },
    "心愿模块": { files: matchFiles(["BookWish", "wish"]) },
    "用户管理模块": { files: matchFiles(["User"]) }, // Admin overlaps with User
    "图书类型模块": { files: matchFiles(["BookType"]) },
    "系统核心功能": { files: matchFiles(["Message", "BorrowTask", "dashboard", "layout", "App", "main", "GlobalException"]) }
};

// Extremely naive but highly effective AST parser for the text trees
// We will look for blocks like:
// 用户模块
// ├── 认证功能
// │   ├── 用户登录
// │   │   ├── 学号密码验证
const treeMatches = [...refContent.matchAll(/(.*模块[（(]?[管理员]*[）)]?)\n[├└]──[\s\S]*?(?=```|\n\n###)/g)];

let mdContent = "# 校园旧书漂流共享系统 - 层级式功能源码全解\n\n";

for (const match of treeMatches) {
    const block = match[0];
    const lines = block.split('\n').filter(l => l.trim().length > 0);
    
    if (lines.length === 0) continue;
    
    const rootModule = lines[0].trim();
    mdContent += `## ${rootModule}\n\n`;
    
    let currentModuleMatchedFiles = [];
    for (const key of Object.keys(globalModuleMap)) {
        if (rootModule.includes(key)) {
            currentModuleMatchedFiles = globalModuleMap[key].files;
            break;
        }
    }
    
    // Parse the tree hierarchy to build paths like Root-Tier1-Tier2-Tier3
    const paths = [];
    let currentPath = [rootModule];
    // To calculate depth accurately based on ASCII prefixes (like │   ├── )
    // Each level is typically 4 characters wide: "├── " or "│   "
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
                break; // Text started
            }
        }
        
        const text = line.substring(p).trim();
        if (text) {
            // Adjust currentPath to match depth
            currentPath = currentPath.slice(0, depth);
            currentPath.push(text);
            paths.push([...currentPath]);
        }
    }
    
    // Now we have paths like ["用户模块", "认证功能", "用户登录", "学号密码验证"]
    // Only output for leaf nodes, or nodes that have no children
    for (let i = 0; i < paths.length; i++) {
        const isLeaf = (i === paths.length - 1) || (paths[i+1].length <= paths[i].length);
        if (isLeaf) {
            const fullPathStr = paths[i].join("-");
            mdContent += `### ${fullPathStr}\n\n`;
            
            // Sub-filtering: try to find the specific file for this leaf node based on its name
            let leafFiles = [];
            const leafName = paths[i][paths[i].length - 1];
            
            if (leafName.includes("登录") || leafName.includes("密码") || leafName.includes("验证")) {
                leafFiles = matchFiles(["UserController", "UserService", "user.js", "login", "Token"]);
            } else if (leafName.includes("注册")) {
                leafFiles = matchFiles(["UserController", "UserService", "user.js", "register"]);
            } else if (leafName.includes("信用")) {
                leafFiles = matchFiles(["User", "BorrowTask"]);
            } else if (leafName.includes("心愿")) {
                leafFiles = matchFiles(["BookWish", "wish"]);
            } else if (leafName.includes("借阅") || leafName.includes("交接") || leafName.includes("归还") || leafName.includes("催还")) {
                leafFiles = matchFiles(["Borrow", "borrow"]);
            } else if (leafName.includes("消息") || leafName.includes("通知")) {
                leafFiles = matchFiles(["Message", "message"]);
            } else {
                // If it's a generic node, just use the parent module files
                leafFiles = currentModuleMatchedFiles;
            }
            
            // Deduplicate
            leafFiles = [...new Set(leafFiles)].slice(0, 3); // Limit to top 3 most relevant files to avoid massive bloat per leaf node
            
            if (leafFiles.length === 0) {
                 mdContent += `> *暂未定位到当前特定功能分支的专属代码文件*\n\n`;
            } else {
                 for (const file of leafFiles) {
                     const relPath = path.relative(path.join(__dirname, '..'), file).replace(/\\/g, '/');
                     const codeStr = fs.readFileSync(file, 'utf-8');
                     
                     // Try to extract a block that looks like the relevant code, otherwise output the whole file
                     let codeBlock = codeStr;
                     // We will output the whole file since the prompt asks for "附上这个文件里的涉及该功能模块的完整代码"
                     // To prevent unreadable files, we'll slice very large ones or just include the full thing.
                     // The user explicitly demanded complete code for the module in this file. 
                     
                     mdContent += `**文件路径**：\`代码/${relPath}\`\n\n`;
                     mdContent += "```" + (path.extname(file).replace('.', '') || 'javascript') + "\n";
                     mdContent += codeBlock + "\n";
                     mdContent += "```\n\n";
                 }
            }
        }
    }
}

// 联动逻辑章节
mdContent += `## 项目功能联动与数据流转说明\n\n`;
mdContent += `每个环节对应的联动逻辑，涵盖完整文件路径及涉及该模块的对应代码。\n\n`;

const flowFiles = [
    { name: "前端借阅发起视图文件", file: "book-frontend/src/views/borrow/index.vue" },
    { name: "前端借阅API封装", file: "book-frontend/src/api/borrow.js" },
    { name: "后端借阅 Controller", file: "book-backend/src/main/java/com/shanzhu/book/web/BorrowController.java" },
    { name: "后端借阅 Service 核心逻辑", file: "book-backend/src/main/java/com/shanzhu/book/service/impl/BorrowServiceImpl.java" },
    { name: "后端借阅 Mapper 数据持久化", file: "book-backend/src/main/resources/mapper/BorrowMapper.xml" },
    { name: "后端定时任务回收监控", file: "book-backend/src/main/java/com/shanzhu/book/task/BorrowTask.java" }
];

for (let i = 0; i < flowFiles.length; i++) {
    mdContent += `### 环节 ${i+1}：${flowFiles[i].name}\n\n`;
    mdContent += `**涉及模块文件路径：** \`代码/${flowFiles[i].file}\`\n\n`;
    const fPath = path.join(__dirname, '..', '代码', flowFiles[i].file);
    const content = fs.existsSync(fPath) ? fs.readFileSync(fPath, 'utf-8') : '代码缺省';
    mdContent += "```" + (path.extname(flowFiles[i].file).replace('.','') || 'javascript') + "\n" + content + "\n```\n\n";
}

if(!fs.existsSync(path.dirname(OUTPUT_FILE))) {
    fs.mkdirSync(path.dirname(OUTPUT_FILE), { recursive: true });
}
fs.writeFileSync(OUTPUT_FILE, mdContent);
console.log('Generation completed with deep functional tree mapping. File size:', mdContent.length);
