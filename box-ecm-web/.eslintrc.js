module.exports = {
  root: true,
  parserOptions: {
    parser: "babel-eslint"
  },
  env: {
    browser: true,
    es6: true,
    node: true
  },
  extends: [
    // https://github.com/vuejs/eslint-plugin-vue#priority-a-essential-error-prevention
    // consider switching to `plugin:vue/strongly-recommended` or `plugin:vue/recommended` for stricter rules.
    "plugin:vue/essential",
    // https://github.com/standard/standard/blob/master/docs/RULES-en.md
    "standard"
  ],
  // required to lint *.vue files
  plugins: ["vue", "html"],
  // add your custom rules here
  rules: {
    // allow async-await
    "generator-star-spacing": "off",
    // allow debugger during development
    "no-debugger": process.env.NODE_ENV === "production" ? "error" : "off",

    "no-extra-boolean-cast": 1,
    //官方文档 http://eslint.org/docs/rules/
    //参数：0 关闭，1 警告，2 错误
    quotes: [2, "double"], //建议使用单引号
    // "no-inner-declarations": [0, "both"],     //不建议在{}代码块内部声明变量或函数
    "no-extra-boolean-cast": 1, //多余的感叹号转布尔型
    semi: [0, "never"], //使用多余的分号
    // 'no-extra-semi': 1, //多余的分号
    "no-extra-parens": 0, //多余的括号
    "no-empty": 1, //空代码块
    //使用前未定义
    "no-use-before-define": [0, "nofunc"],

    complexity: [0, 10], //圈复杂度大于*

    //定义数组或对象最后多余的逗号
    "comma-dangle": [0, "never"],

    // 不允许对全局变量赋值,如 window = 'abc'
    "no-global-assign": [
      "error",
      {
        // 定义例外
        // "exceptions": ["Object"]
      }
    ],
    eqeqeq: 0, //三等号
    "no-var": 0, //用let或const替代var
    "no-const-assign": 2, //不允许const重新赋值
    "no-class-assign": 2, //不允许对class重新赋值
    "no-debugger": 1, //debugger 调试代码未删除
    "no-console": 0, //console 未删除
    "no-constant-condition": 2, //常量作为条件
    "no-dupe-args": 2, //参数重复
    "no-dupe-keys": 2, //对象属性重复
    "no-duplicate-case": 2, //case重复
    "no-empty-character-class": 2, //正则无法匹配任何值
    "no-invalid-regexp": 2, //无效的正则
    "no-func-assign": 2, //函数被赋值
    "valid-typeof": 1, //无效的类型判断
    "no-unreachable": 2, //不可能执行到的代码
    "no-unexpected-multiline": 2, //行尾缺少分号可能导致一些意外情况
    "no-sparse-arrays": 1, //数组中多出逗号
    "no-shadow-restricted-names": 2, //关键词与命名冲突
    "no-undef": 1, //变量未定义
    "no-unused-vars": 1, //变量定义后未使用
    "no-cond-assign": 2, //条件语句中禁止赋值操作
    "no-native-reassign": 2, //禁止覆盖原生对象
    "no-mixed-spaces-and-tabs": 0,

    //代码风格优化 --------------------------------------
    "no-irregular-whitespace": 0,
    "no-else-return": 0, //在else代码块中return，else是多余的
    "no-multi-spaces": 0, //不允许多个空格

    //object直接量建议写法 : 后一个空格前面不留空格
    "key-spacing": [
      0,
      {
        beforeColon: false,
        afterColon: true
      }
    ],

    "block-scoped-var": 1, //变量应在外部上下文中声明，不应在{}代码块中
    "consistent-return": 1, //函数返回值可能是不同类型
    "accessor-pairs": 1, //object getter/setter方法需要成对出现

    //换行调用对象方法  点操作符应写在行首
    "dot-location": [1, "property"],
    "no-lone-blocks": 1, //多余的{}嵌套
    "no-labels": 1, //无用的标记
    "no-extend-native": 1, //禁止扩展原生对象
    "no-floating-decimal": 1, //浮点型需要写全 禁止.1 或 2.写法
    "no-loop-func": 1, //禁止在循环体中定义函数
    "no-new-func": 1, //禁止new Function(...) 写法
    "no-self-compare": 1, //不允与自己比较作为条件
    "no-sequences": 1, //禁止可能导致结果不明确的逗号操作符
    "no-throw-literal": 1, //禁止抛出一个直接量 应是Error对象

    //不允return时有赋值操作
    "no-return-assign": [1, "always"],

    //不允许重复声明
    "no-redeclare": [
      1,
      {
        builtinGlobals: true
      }
    ],

    //不执行的表达式
    "no-unused-expressions": [
      0,
      {
        allowShortCircuit: true,
        allowTernary: true
      }
    ],
    "no-useless-call": 1, //无意义的函数call或apply
    "no-useless-concat": 1, //无意义的string concat
    "no-void": 1, //禁用void
    "no-with": 1, //禁用with
    "space-infix-ops": 0, //操作符前后空格

    //jsdoc
    "valid-jsdoc": [
      0,
      {
        requireParamDescription: true,
        requireReturnDescription: true
      }
    ],

    //标记未写注释
    "no-warning-comments": [
      1,
      {
        terms: ["todo", "fixme", "any other term"],
        location: "anywhere"
      }
    ],
    curly: 0 //if、else、while、for代码块用{}包围
    // //
    // //
    // // 可能的错误
    // //
    // // 禁止重复的二级键名
    // // @off 没必要限制
    // 'vue/no-dupe-keys': 'off',
    // // 禁止出现语法错误
    // 'vue/no-parsing-error': 'error',
    // // 禁止覆盖保留字
    // 'vue/no-reserved-keys': 'error',
    // // 组件的 data 属性的值必须是一个函数
    // // @off 没必要限制
    // 'vue/no-shared-component-data': 'off',
    // // 禁止 <template> 使用 key 属性
    // // @off 太严格了
    // 'vue/no-template-key': 'off',
    // // render 函数必须有返回值
    // 'vue/require-render-return': 'error',
    // // prop 的默认值必须匹配它的类型
    // // @off 太严格了
    // 'vue/require-valid-default-prop': 'off',
    // // 计算属性必须有返回值
    // 'vue/return-in-computed-property': 'error',
    // // template 的根节点必须合法
    // 'vue/valid-template-root': 'error',
    // // v-bind 指令必须合法
    // 'vue/valid-v-bind': 'error',
    // // v-cloak 指令必须合法
    // 'vue/valid-v-cloak': 'error',
    // // v-else-if 指令必须合法
    // 'vue/valid-v-else-if': 'error',
    // // v-else 指令必须合法
    // 'vue/valid-v-else': 'error',
    // // v-for 指令必须合法
    // 'vue/valid-v-for': 'error',
    // // v-html 指令必须合法
    // 'vue/valid-v-html': 'error',
    // // v-if 指令必须合法
    // 'vue/valid-v-if': 'error',
    // // v-model 指令必须合法
    // 'vue/valid-v-model': 'error',
    // // v-on 指令必须合法
    // 'vue/valid-v-on': 'error',
    // // v-once 指令必须合法
    // 'vue/valid-v-once': 'error',
    // // v-pre 指令必须合法
    // 'vue/valid-v-pre': 'error',
    // // v-show 指令必须合法
    // 'vue/valid-v-show': 'error',
    // // v-text 指令必须合法
    // 'vue/valid-v-text': 'error',

    // //
    // //
    // // 最佳实践
    // //
    // // @fixable html 的结束标签必须符合规定
    // // @off 有的标签不必严格符合规定，如 <br> 或 <br/> 都应该是合法的
    // 'vue/html-end-tags': 'off',
    // // 计算属性禁止包含异步方法
    // 'vue/no-async-in-computed-properties': 'error',
    // // 禁止出现难以理解的 v-if 和 v-for
    // 'vue/no-confusing-v-for-v-if': 'error',
    // // 禁止出现重复的属性
    // 'vue/no-duplicate-attributes': 'error',
    // // 禁止在计算属性中对属性修改
    // // @off 太严格了
    // 'vue/no-side-effects-in-computed-properties': 'off',
    // // 禁止在 <textarea> 中出现 {{message}}
    // 'vue/no-textarea-mustache': 'error',
    // // 组件的属性必须为一定的顺序
    // 'vue/order-in-components': 'error',
    // // <component> 必须有 v-bind:is
    // 'vue/require-component-is': 'error',
    // // prop 必须有类型限制
    // // @off 没必要限制
    // 'vue/require-prop-types': 'off',
    // // v-for 指令的元素必须有 v-bind:key
    // 'vue/require-v-for-key': 'error',

    // //
    // //
    // // 风格问题
    // //
    // // @fixable 限制自定义组件的属性风格
    // // @off 没必要限制
    // 'vue/attribute-hyphenation': 'off',
    // // html 属性值必须用双引号括起来
    // 'vue/html-quotes': 'error',
    // // @fixable 没有内容时，组件必须自闭和
    // // @off 没必要限制
    // 'vue/html-self-closing': 'off',
    // // 限制每行允许的最多属性数量
    // // @off 没必要限制
    // 'vue/max-attributes-per-line': 'off',
    // // @fixable 限制组件的 name 属性的值的风格
    // // @off 没必要限制
    // 'vue/name-property-casing': 'off',
    // // @fixable 禁止出现连续空格
    // 'vue/no-multi-spaces': 'error',
    // // @fixable 限制 v-bind 的风格
    // // @off 没必要限制
    // 'vue/v-bind-style': 'off',
    // // @fixable 限制 v-on 的风格
    // // @off 没必要限制
    // 'vue/v-on-style': 'off',

    // //
    // //
    // // 变量
    // //
    // // 定义了的 jsx element 必须使用
    // 'vue/jsx-uses-vars': 'error'
  }
};
// module.exports = {
//     env: {
//         browser: true,
//         commonjs: true,
//         es6: true
//     },
//     extends: 'eslint:recommended',
//     parserOptions: {
//         sourceType: 'module'
//     },
//     rules: {
//         indent: ['error', 4],
//         'linebreak-style': ['error', 'windows'],
//         quotes: ['error', 'single'],
//         semi: [0, 'never']
//     }
// };
