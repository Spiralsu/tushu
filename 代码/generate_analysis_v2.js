const fs = require('fs');
const path = require('path');

const BACKEND_DIR = path.join(__dirname, 'book-backend');
const FRONTEND_DIR = path.join(__dirname, 'book-frontend');
const REF_FILE = path.join(__dirname, '../.md/specs/project-analysis/详细项目分析.md');
const OUTPUT_FILE = path.join(__dirname, '../.md/specs/project-analysis/详细项目源码全面剖析_终极版.md');

// Utility to find files
function findFiles(dir, extensions, fileList = []) {
    if (!fs.existsSync(dir)) return fileList;
    const files = fs.readdirSync(dir);
    for (const file of files) {
        if (file === 'node_modules' || file === '.git' || file === 'target' || file === 'dist' || file.startsWith('.')) continue;
        const filePath = path.join(dir, file);
        const stat = fs.statSync(filePath);
        if (stat.isDirectory()) {
            findFiles(filePath, extensions, fileList);
        } else {
            const ext = path.extname(file).toLowerCase();
            if (extensions.includes(ext)) {
                fileList.push(filePath);
            }
        }
    }
    return fileList;
}

const javaExts = ['.java', '.xml', '.yml'];
const jsExts = ['.js', '.vue'];
const backendFiles = findFiles(BACKEND_DIR, javaExts);
const frontendFiles = findFiles(FRONTEND_DIR, jsExts);
const allFiles = [...backendFiles, ...frontendFiles];

// The core modules
const modules = [
    {
        name: "2.1 用户模块",
        regex: /### 2\.1 用户模块\s*```[\s\S]*?\n([\s\S]*?)\n```/,
        keywords: ["User", "login", "register", "password", "auth", "TokenProcessor"]
    },
    {
        name: "2.2 图书模块",
        regex: /### 2\.2 图书模块\s*```[\s\S]*?\n([\s\S]*?)\n```/,
        keywords: ["BookInfo", "bookinfo", "uploadController"]
    },
    {
        name: "2.3 借阅模块",
        regex: /### 2\.3 借阅模块\s*```[\s\S]*?\n([\s\S]*?)\n```/,
        keywords: ["Borrow", "borrow"]
    },
    {
        name: "2.4 心愿模块",
        regex: /### 2\.4 心愿模块\s*```[\s\S]*?\n([\s\S]*?)\n```/,
        keywords: ["BookWish", "wish", "wish.js"]
    },
    {
        name: "2.5 用户管理模块（管理员）",
        regex: /### 2\.5 用户管理模块（管理员）\s*```[\s\S]*?\n([\s\S]*?)\n```/,
        // Specific to admin user management
        keywords: ["UserManager", "AdminUser"] // We'll manually assign if needed, or rely on core.
    },
    {
        name: "2.6 图书类型模块（管理员）",
        regex: /### 2\.6 图书类型模块（管理员）\s*```[\s\S]*?\n([\s\S]*?)\n```/,
        keywords: ["BookType", "booktype"]
    },
    {
        name: "2.7 系统核心功能",
        regex: /### 2\.7 系统核心功能\s*```[\s\S]*?\n([\s\S]*?)\n```/,
        keywords: ["Message", "message", "BorrowTask", "dashboard", "layout", "App", "main", "GlobalException", "R", "WechatPush", "CorsConfig", "WebMvcConfig", "Router", "Store", "utils", "path", "Token", "permission", "settings"]
    }
];

const moduleFilesMap = {};
for (const mod of modules) moduleFilesMap[mod.name] = [];
const usedFiles = new Set();

for (const file of allFiles) {
    if (usedFiles.has(file)) continue;

    const baseName = path.basename(file);
    const lowerName = baseName.toLowerCase();
    
    if(lowerName.includes("package-lock") || lowerName.includes("iconfont") || lowerName.includes("jest") || lowerName.includes("babel")) continue;

    let assigned = false;
    // Map controller to admin if needed (e.g. user admin logic is usually just UserController, we'll keep it simple)
    for (const mod of modules) {
        if (!mod.keywords) continue;
        for (const kw of mod.keywords) {
            if (lowerName.includes(kw.toLowerCase())) {
                moduleFilesMap[mod.name].push(file);
                usedFiles.add(file);
                assigned = true;
                break;
            }
        }
        if (assigned) break;
    }
    
    if (!assigned) {
        moduleFilesMap["2.7 系统核心功能"].push(file);
        usedFiles.add(file);
    }
}

// Read reference file
const refContent = fs.existsSync(REF_FILE) ? fs.readFileSync(REF_FILE, 'utf-8') : '';

let markdownContent = "# 校园旧书漂流共享系统 - 完整源代码深度剖析\n\n" +
"> 本文档基于完整的项目源码生成，通过扫描所有后端JAVA、前端JS/VUE、及配置XML文件，列举了系统的各个功能模块、每个模块涉及的具体文件绝对/相对路径，并完整附上了这部分涉及的基础【完整代码】。\n\n" +
"---\n\n" +
"## 目录\n" +
"- [第一部分：功能模块层级展开](#第一部分功能模块层级展开)\n" +
"- [第二部分：项目功能联动与数据流转说明](#第二部分项目功能联动与数据流转说明)\n\n" +
"---\n\n" +
"## 第一部分：功能模块层级展开\n\n";

for (const mod of modules) {
    markdownContent += "### " + mod.name + "\n\n";
    
    // Extract tree from reference
    const match = refContent.match(mod.regex);
    if (match && match[1]) {
        markdownContent += "```text\n" + match[1] + "\n```\n\n";
    } else {
        markdownContent += "> 模块层级结构未能从参考文档成功提取。\n\n";
    }
    
    markdownContent += "#### 涉及的代码文件清单：\n";
    
    const files = moduleFilesMap[mod.name];
    if (files.length === 0) {
        markdownContent += "*未检索到相关文件*\n\n";
        continue;
    }
    
    for (const file of files) {
        const relPath = path.relative(path.join(__dirname, '..'), file).replace(/\\/g, '/');
        markdownContent += "- `{\n完整的文件相对路径: " + relPath + "\n}`\n";
    }
    markdownContent += "\n#### 相应文件的【完整代码】：\n\n";
    
    for (const file of files) {
        const relPath = path.relative(path.join(__dirname, '..'), file).replace(/\\/g, '/');
        const ext = path.extname(file).replace('.', '');
        const code = fs.readFileSync(file, 'utf-8');
        
        markdownContent += "##### 完整的文件相对路径：`" + relPath + "`\n\n";
        markdownContent += "```" + (ext === 'vue' ? 'vue' : (ext === 'java' ? 'java' : (ext === 'xml' ? 'xml' : 'javascript'))) + "\n";
        markdownContent += code + "\n";
        markdownContent += "```\n\n";
    }
    markdownContent += "---\n\n";
}

markdownContent += "## 第二部分：项目功能联动与数据流转说明\n\n" +
"为清晰展现本项目的前后端请求和数据流转链路，本部分以**图书借阅流转**及**借阅超时系统自动取消**的核心环节为例，讲述系统联动。\n\n" +

"### 环节一：前端发起图书借阅请求\n" +
"用户在前端视图点击“申请”，触发借阅弹框并确认提交，最终调用API服务发送请求至后端。\n" +
"**当前环节涉及文件完整相对路径：**\n" +
"- `代码/book-frontend/src/views/borrow/index.vue`\n" +
"- `代码/book-frontend/src/api/borrow.js`\n\n";

const vBorrowVue = path.join(__dirname, 'book-frontend/src/views/borrow/index.vue');
const vBorrowJs = path.join(__dirname, 'book-frontend/src/api/borrow.js');

markdownContent += "#### 涉及文件：`代码/book-frontend/src/views/borrow/index.vue` 完整代码\n\n```vue\n" +
(fs.existsSync(vBorrowVue) ? fs.readFileSync(vBorrowVue, 'utf-8') : '代码缺省或路径不符') + "\n```\n\n";

markdownContent += "#### 涉及文件：`代码/book-frontend/src/api/borrow.js` 完整代码\n\n```javascript\n" +
(fs.existsSync(vBorrowJs) ? fs.readFileSync(vBorrowJs, 'utf-8') : '代码缺省或路径不符') + "\n```\n\n";

markdownContent += "### 环节二：后端控制器拦截借阅请求响应\n" +
"后端获取从前端传递的参数结构（比如图书编号、借阅时长等），在Controller层中路由后抛入Service层实施真实逻辑。\n" +
"**当前环节涉及文件完整相对路径：**\n" +
"- `代码/book-backend/src/main/java/com/shanzhu/book/web/BorrowController.java`\n\n";

const vBorrowCtrl = path.join(__dirname, 'book-backend/src/main/java/com/shanzhu/book/web/BorrowController.java');
markdownContent += "#### 涉及文件：`代码/book-backend/src/main/java/com/shanzhu/book/web/BorrowController.java` 完整代码\n\n```java\n" +
(fs.existsSync(vBorrowCtrl) ? fs.readFileSync(vBorrowCtrl, 'utf-8') : '代码缺省或路径不符') + "\n```\n\n";

markdownContent += "### 环节三：借阅业务处理层深入 (含自动发送消息系统联动)\n" +
"BorrowService层不仅完成了数据库事务（更新借阅表，扣库存）还联动了Message通知、微信推送通道，实现了数据高度协同。\n" +
"**当前环节涉及文件完整相对路径：**\n" +
"- `代码/book-backend/src/main/java/com/shanzhu/book/service/impl/BorrowServiceImpl.java`\n\n";

const vBorrowSvc = path.join(__dirname, 'book-backend/src/main/java/com/shanzhu/book/service/impl/BorrowServiceImpl.java');
markdownContent += "#### 涉及文件：`代码/book-backend/src/main/java/com/shanzhu/book/service/impl/BorrowServiceImpl.java` 完整代码\n\n```java\n" +
(fs.existsSync(vBorrowSvc) ? fs.readFileSync(vBorrowSvc, 'utf-8') : '代码缺省或路径不符') + "\n```\n\n";

markdownContent += "### 环节四：超时自动回收功能 (基于定时任务联动数据库更新)\n" +
"Spring的定时任务层时刻侦听，在规定时间内没有发起确认交接申请则强制收回到库存库中，并在最后一步通过Mapper真正将所有变更落库。\n" +
"**当前环节涉及文件完整相对路径：**\n" +
"- `代码/book-backend/src/main/java/com/shanzhu/book/task/BorrowTask.java`\n" +
"- `代码/book-backend/src/main/resources/mapper/BorrowMapper.xml`\n\n";

const vBorrowTask = path.join(__dirname, 'book-backend/src/main/java/com/shanzhu/book/task/BorrowTask.java');
const vBorrowMapper = path.join(__dirname, 'book-backend/src/main/resources/mapper/BorrowMapper.xml');

markdownContent += "#### 涉及文件：`代码/book-backend/src/main/java/com/shanzhu/book/task/BorrowTask.java` 完整代码\n\n```java\n" +
(fs.existsSync(vBorrowTask) ? fs.readFileSync(vBorrowTask, 'utf-8') : '代码缺省或路径不符') + "\n```\n\n";

markdownContent += "#### 涉及文件：`代码/book-backend/src/main/resources/mapper/BorrowMapper.xml` 完整代码\n\n```xml\n" +
(fs.existsSync(vBorrowMapper) ? fs.readFileSync(vBorrowMapper, 'utf-8') : '代码缺省或路径不符') + "\n```\n\n";

markdownContent += "> **至此，从前端点击行为-响应-业务落库-超时管控 的全部链路与项目源码绑定呈现完毕。由于涉及完整代码生成，文件篇幅巨大。**\n";

if(!fs.existsSync(path.dirname(OUTPUT_FILE))) {
    fs.mkdirSync(path.dirname(OUTPUT_FILE), { recursive: true });
}
fs.writeFileSync(OUTPUT_FILE, markdownContent);
console.log('Document generation successful! Path: ' + OUTPUT_FILE);
