# Problem with rendering react-native-skia canvas & react-native-paper

This project is a copy of `../0009_portfolio_skia_render`, and as such the `:portfolio` build is 
not relevant at the moment.

This copy has `:js-provider :external` and a webpack bundling step.

A discussion on slack:
https://clojurians.slack.com/archives/C6N245JGG/p1696512061945189

## Standalone:

```
yarn install
yarn webpack --config webpack.config.js
yarn shadow-cljs -A:standalone watch :standalone
```

Visit http://localhost:8082/standalone/ in the browser

Nothing will happen as console log error:

```
Uncaught TypeError: Cannot read properties of undefined (reading 'TypefaceFontProvider')
    at JsiSkFontMgrFactory.System (JsiSkFontMgrFactory.js:9:1)
```

``` js
import { Host } from "./Host";
import { JsiSkFontMgr } from "./JsiSkFontMgr";
export class JsiSkFontMgrFactory extends Host {
  constructor(CanvasKit) {
    super(CanvasKit);
  }

  System() {
    const fontMgr = this.CanvasKit.TypefaceFontProvider.Make();
                               ^^^^^^^^^
```

Many parts of `react-native-skia` need to be split from the canvas loading parts which needs to happen first. However where as in `../0009_portfolio_skia_render` this was done by `shadow-cljs` code-splitting, now webpack bundling needs to also do this step, but there is only one `:external-index`, not one for each module. :grimacing: 
