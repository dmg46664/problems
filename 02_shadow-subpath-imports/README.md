

```
yarn shadow-cljs compile script                                                   main !1 ?2  1 ✘  02:47:13
yarn run v1.22.17
warning package.json: No license field
$ /Users/dmg46664/projects/problems/02_shadow-subpath-imports/node_modules/.bin/shadow-cljs compile script
shadow-cljs - config: /Users/dmg46664/projects/problems/02_shadow-subpath-imports/shadow-cljs.edn
shadow-cljs - starting via "clojure"
[:script] Compiling ...
The required JS dependency "#ansi-styles" is not available, it was required by "node_modules/chalk/source/index.js".

Dependency Trace:
	hello.cljs
	shadow.js.shim.module$chalk$default.js
	node_modules/chalk/source/index.js

Searched for npm packages in:
	/Users/dmg46664/projects/problems/02_shadow-subpath-imports/node_modules

See: https://shadow-cljs.github.io/docs/UsersGuide.html#npm-install

error Command failed with exit code 1.
info Visit https://yarnpkg.com/en/docs/cli/run for documentation about this command.
```

The problem seems to be subpath imports


https://nodejs.org/api/packages.html#subpath-imports
