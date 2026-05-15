const fs = require('fs');
const path = require('path');

const OUTPUT_FILE = path.join(__dirname, '../.md/specs/project-analysis/项目层级功能源码完全映射.md');

// Helper to reliably read file
function readFile(relPath) {
    const fullPath = path.join(__dirname, relPath);
    if (fs.existsSync(fullPath)) {
        return fs.readFileSync(fullPath, 'utf-8');
    }
    return `// 文件不存在: ${relPath}`;
}

// Helper to extract a method block from Java/JS/Vue roughly.
// If regex doesn't match, we fallback to returning the whole file,
// or a message saying "完整文件如下".
function extractBlock(content, sigRegex) {
    if (!sigRegex) return content; 
    const match = content.match(sigRegex);
    if (match) {
        return match[0];
    }
    return content; 
}

const tree = [
    {
        tier1: "1. 用户模块",
        tier2: "1.1 认证功能",
        tier3: [
            {
                name: "1.1.1 用户登录 (学号密码验证、身份校验、Token生成)",
                files: [
                    { path: "book-backend/src/main/java/com/shanzhu/book/web/UserController.java", regex: /(@PostMapping\(value = "\/login"\)[\s\S]*?public R login[\s\S]*?})/ },
                    { path: "book-backend/src/main/java/com/shanzhu/book/service/impl/UserServiceImpl.java", regex: /(public R userLogin[\s\S]*?return R\.ok.*?;[\s\S]*?})/ },
                    { path: "book-backend/src/main/java/com/shanzhu/book/utils/TokenProcessor.java", regex: /(public String generateToken\(\)[\s\S]*?})/ },
                    { path: "book-frontend/src/api/user.js", regex: /(\/\/\s*登录[\s\S]*?export function login[\s\S]*?})/ },
                    { path: "book-frontend/src/views/login/index.vue", regex: /(handleLogin\(\) {[\s\S]*?}\)?)/ }
                ]
            },
            {
                name: "1.1.2 用户注册 (学号唯一性、默认状态)",
                files: [
                    { path: "book-backend/src/main/java/com/shanzhu/book/web/UserController.java", regex: /(@PostMapping\(value = "\/registerUser"\)[\s\S]*?})/ },
                    { path: "book-backend/src/main/java/com/shanzhu/book/service/impl/UserServiceImpl.java", regex: /(public R userRegister[\s\S]*?})/ },
                    { path: "book-frontend/src/api/user.js", regex: /(\/\/\s*注册[\s\S]*?export function register[\s\S]*?})/ }
                ]
            },
            {
                name: "1.1.3 退出登录 (清除Token)",
                files: [
                    { path: "book-backend/src/main/java/com/shanzhu/book/web/UserController.java", regex: /(@RequestMapping\(value = "\/logout"\)[\s\S]*?})/ },
                    { path: "book-backend/src/main/java/com/shanzhu/book/utils/TokenProcessor.java", regex: /(public void removeUser[\s\S]*?})/ }
                ]
            }
        ]
    },
    {
        tier1: "1. 用户模块",
        tier2: "1.2 个人信息管理",
        tier3: [
            {
                name: "1.2.1 个人信息查看与修改密码",
                files: [
                    { path: "book-backend/src/main/java/com/shanzhu/book/web/UserController.java", regex: /(@RequestMapping\(value = "\/info"\)[\s\S]*?})/ },
                    { path: "book-backend/src/main/java/com/shanzhu/book/service/impl/UserServiceImpl.java", regex: /(public void setPassword[\s\S]*?})/ }
                ]
            }
        ]
    },
    {
        tier1: "2. 图书模块",
        tier2: "2.1 图书浏览与检索",
        tier3: [
            {
                name: "2.1.1 图书分页展示与条件搜索 (书名模糊、类型筛选)",
                files: [
                    { path: "book-backend/src/main/java/com/shanzhu/book/web/BookInfoController.java", regex: /(@GetMapping\(value = "\/queryBookInfosByPage"\)[\s\S]*?})/ },
                    { path: "book-backend/src/main/resources/mapper/BookInfoMapper.xml", regex: /(<select id="selectBySearch"[\s\S]*?<\/select>)/ },
                    { path: "book-frontend/src/views/bookinfo/index.vue", regex: /(getList\(\) {[\s\S]*?})/ }
                ]
            }
        ]
    },
    {
        tier1: "2. 图书模块",
        tier2: "2.2 图书发布",
        tier3: [
            {
                name: "2.2.1 填写图书信息与封面上传",
                files: [
                    { path: "book-backend/src/main/java/com/shanzhu/book/web/uploadController.java", regex: /(@PostMapping\("\/uploadBookImage"\)[\s\S]*?})/ },
                    { path: "book-backend/src/main/java/com/shanzhu/book/service/impl/BookInfoServiceImpl.java", regex: /(public Integer addBookInfo[\s\S]*?})/ }
                ]
            }
        ]
    },
    {
        tier1: "3. 借阅模块",
        tier2: "3.1 借阅申请与审核",
        tier3: [
            {
                name: "3.1.1 申请借阅 (检查信用分、库存、填理由)",
                files: [
                    { path: "book-backend/src/main/java/com/shanzhu/book/web/BorrowController.java", regex: /(@PostMapping\("\/borrowBook"\)[\s\S]*?})/ },
                    { path: "book-backend/src/main/java/com/shanzhu/book/service/impl/BorrowServiceImpl.java", regex: /(public R addBorrow[\s\S]*?return R\.ok.*?;[\s\S]*?})/ }
                ]
            },
            {
                name: "3.1.2 审核申请 (同意扣库存/驳回)",
                files: [
                    { path: "book-backend/src/main/java/com/shanzhu/book/web/BorrowController.java", regex: /(@PostMapping\("\/audit"\)[\s\S]*?})/ },
                    { path: "book-backend/src/main/java/com/shanzhu/book/service/impl/BorrowServiceImpl.java", regex: /(public R auditBorrow[\s\S]*?return R\.ok.*?;[\s\S]*?})/ }
                ]
            }
        ]
    },
    {
        tier1: "3. 借阅模块",
        tier2: "3.2 线下交接与归还",
        tier3: [
            {
                name: "3.2.1 验证暗号与交接流转",
                files: [
                    { path: "book-backend/src/main/java/com/shanzhu/book/web/BorrowController.java", regex: /(@PostMapping\("\/verifyCode"\)[\s\S]*?})/ },
                    { path: "book-backend/src/main/java/com/shanzhu/book/service/impl/BorrowServiceImpl.java", regex: /(public R verifyCode[\s\S]*?return R\.ok.*?;[\s\S]*?})/ }
                ]
            },
            {
                name: "3.2.2 归还图书与所有权转移",
                files: [
                    { path: "book-backend/src/main/java/com/shanzhu/book/web/BorrowController.java", regex: /(@PostMapping\("\/returnBook"\)[\s\S]*?})/ },
                    { path: "book-backend/src/main/java/com/shanzhu/book/service/impl/BorrowServiceImpl.java", regex: /(public R returnBook[\s\S]*?return R\.ok.*?;[\s\S]*?})/ }
                ]
            }
        ]
    },
    {
        tier1: "3. 借阅模块",
        tier2: "3.3 系统判定机制",
        tier3: [
            {
                name: "3.3.1 定时任务 (交接超时取消、逾期扣分)",
                files: [
                    { path: "book-backend/src/main/java/com/shanzhu/book/task/BorrowTask.java", regex: /(public void checkBorrowTimeout[\s\S]*?})/ }
                ]
            }
        ]
    },
    {
        tier1: "4. 心愿模块",
        tier2: "4.1 心愿流转",
        tier3: [
            {
                name: "4.1.1 发布心愿与自动匹配满足",
                files: [
                    { path: "book-backend/src/main/java/com/shanzhu/book/web/BookWishController.java", regex: /(@PostMapping\("\/add"\)[\s\S]*?})/ },
                    { path: "book-backend/src/main/java/com/shanzhu/book/service/impl/BookWishServiceImpl.java", regex: /(public void fulfillWishIfMatched[\s\S]*?})/ }
                ]
            }
        ]
    }
];

let md = "# 专属项目层级分解与功能源码映射剖析\n\n";
md += "> 本文档完全遵循层级式功能分解树的要求，针对当前项目源码的实际逻辑，将**具体的业务处理代码段**拆解并挂载到每一个最小功能层级（叶子节点）下方，并标注【完整的文件相对路径】。\n\n";

let currentTier1 = "";
let currentTier2 = "";

for (const group of tree) {
    if (group.tier1 !== currentTier1) {
        md += `## ${group.tier1}\n\n`;
        currentTier1 = group.tier1;
    }
    if (group.tier2 !== currentTier2) {
        md += `### ${group.tier2}\n\n`;
        currentTier2 = group.tier2;
    }
    
    for (const leaf of group.tier3) {
        md += `#### ${group.tier1.split(' ')[1]} - ${group.tier2.split(' ')[1]} - ${leaf.name.split(' (')[0]}\n\n`;
        md += `**功能点说明**：${leaf.name}\n\n`;
        
        for (const fileDef of leaf.files) {
            md += `**代码/代码文件路径：** \`代码/${fileDef.path}\`\n\n`;
            md += `*涉及该功能模块的【核心代码】：*\n`;
            
            const fileContent = readFile(fileDef.path);
            let extracted = extractBlock(fileContent, fileDef.regex);
            
            let ext = path.extname(fileDef.path).replace('.', '');
            if (ext === '') ext = 'javascript';
            
            md += "```" + ext + "\n";
            md += extracted + "\n";
            md += "```\n\n";
        }
    }
}

// 联动逻辑章节
md += `## 项目功能联动与数据流转说明\n\n`;
md += `以下详细剖析整个项目中前、后端及数据库的联动逻辑。\n\n`;

const linkFlow = [
    {
        step: "环节一：从前端发起的路由与API封装",
        desc: "以图书借阅为例。用户在界面发起操作，Vue组件捕获事件，调用 axios 封装好的 API 请求传递给后端网关。",
        files: [
             { path: "book-frontend/src/api/borrow.js", regex: /(export function borrowBook[\s\S]*?})/ }
        ]
    },
    {
        step: "环节二：后端 Controller 控制器接收与预处理",
        desc: "后端的 Controller 层拦截到请求（带有 Token 拦截器的前置校验），解析 JSON 参数并派发至 Service 服务层处理。",
        files: [
            { path: "book-backend/src/main/java/com/shanzhu/book/web/BorrowController.java", regex: /(@PostMapping\("\/borrowBook"\)[\s\S]*?})/ }
        ]
    },
    {
        step: "环节三：Service 层执行核心业务与风控",
        desc: "Service层是最复杂的组装中心。包括校验库存（BookInfoMapper）、校验信用分（UserMapper），计算风控，甚至调用微信推送工具（WechatPushUtils）进行多端通知。",
        files: [
            { path: "book-backend/src/main/java/com/shanzhu/book/service/impl/BorrowServiceImpl.java", regex: /(public R addBorrow[\s\S]*?return R\.ok.*?;[\s\S]*?})/ }
        ]
    },
    {
        step: "环节四：Mapper 持久层将结果落盘",
        desc: "经过严格计算后，利用 MyBatis Mapper 将结果更新至借阅状态表、用户积分表和系统消息表中，实现数据闭环流转。",
        files: [
            { path: "book-backend/src/main/resources/mapper/BorrowMapper.xml", regex: /(<insert id="insert"[\s\S]*?<\/insert>)/ }
        ]
    }
];

for (const flow of linkFlow) {
    md += `### ${flow.step}\n\n`;
    md += `**联动逻辑说明：** ${flow.desc}\n\n`;
    for (const fileDef of flow.files) {
         md += `**执行该机制的映射文件路径：** \`代码/${fileDef.path}\`\n\n`;
         md += "```" + path.extname(fileDef.path).replace('.','') + "\n";
         md += extractBlock(readFile(fileDef.path), fileDef.regex) + "\n";
         md += "```\n\n";
    }
}

md += "> 项目功能闭环演示完毕，所有代码片段均取自本项目的当前真实工作环境。\n";

if(!fs.existsSync(path.dirname(OUTPUT_FILE))) {
    fs.mkdirSync(path.dirname(OUTPUT_FILE), { recursive: true });
}
fs.writeFileSync(OUTPUT_FILE, md);
console.log('Document generation successful! Path: ' + OUTPUT_FILE);
