# Test Manager Front
`Test Manager Front` is the core front service of Choerodon. The service provide users with agile continuous testing tools. Improve test flexibility and visibility. Optimize project test management methods.

## Features
- **Test Case Management**
- **Test Plan**
- **Test Execute Management**
- **Test Result Management**
- **Test Status Management**
- **Automation Test**
  
## Environment Support

Modern browsers and Internet Explorer 10+（Currently, it is best to browse through Google.）

## Directory structure

The following is the main directory structure:

```
 ├── Dockerfile
 ├── chart
 ├── config.js
 ├── testManager
 │   ├── eslintrc.json
 │   ├── stylelinktrc.json
 │   ├── package.json
 │   └── src
 │       └── app
 │           └── testManager
 │               ├── api                # some common api
 │               ├── assets             # some static resources
 |               ├── common             # utils and constant
 │               ├── components         # some components use in the project
 │               ├── config
 │               │   ├── Menu.yml       # menu config
 │               │   └── language
 │               │   └── dashboard      # dashboard card config
 │               ├── containers
 │               │   ├── TestManagerIndex.js  # router
 │               │   └── project        # main core
 |               ├── dashboard          # dashboard core
 │               ├── locale
 │               │   ├── en_US.js
 │               │   └── zh_CN.js
 │               ├── stores
 │                   └── project        # mobx stores
 └── favicon.ico

```

* `assets` store static resources.
* `containers` store the front pages
* `stores` mobx store the data needed for the front page
* `components` store in public components
* `config` store `Menu.yml` configuration file (including code and icon of menu, jump into Route, menu permissions) and language in Chinese and English yml (`zh.yml`, `en.yml`). Add dashboard folder that description the dashboard cards.
* `dashboard` store the dashboard cards core
* `config.js` configuration webpack
* `utils` store common function

See more at [doc](http://choerodon.io/zh/docs/development-guide/front).

## Development

### Clone the project:
```
git clone https://github.com/choerodon/choerodon-front-test-manager
```

### Enter the directory install dependencies:
Note:This project used a lot of properties such as ES6/7, so node 6.0.0+ is required.

```
cd testManager
npm install
```
### Run

``` js
cd testManager
npm start
```
Open browser and visit http://localhost:9090.

## Links

- [Choerodon](http://choerodon.io)
- [Choerodon Forum](http://forum.choerodon.io/)

## Reporting Issues
If you find any shortcomings or bugs, please describe them in the [issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).

## How to Contribute
We welcome all contributions. You can submit any ideas as [pull requests](https://github.com/choerodon/choerodon/pulls). [Follow](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) to know for more information on how to contribute.
 