const path = require('path');

module.exports = {
    mode: 'development',
    // externalise everything but stuff in brackets
    externals: [/^(?!(react-native-vector-icons))$/],

    // Path to the entry file, change it according to the path you have
    entry: path.join(__dirname, 'dist','externs.js'),

    // Path for the output files
    output: {
        path: path.join(__dirname, 'public', 'js'),
        filename: 'rn-paper-and-icons.js',
    },

    // Enable source map support
    devtool: 'source-map',

    

    // Loaders and resolver config
    module: {
        rules: [

            {
                test: /\.(jpg|png|woff|woff2|eot|ttf|svg)$/,
                type: 'asset/resource'
            },

            {
                test: /\.js$/,
                exclude: /node_modules[/\\](?!react-native-vector-icons)/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        // Disable reading babel configuration
                        babelrc: false,
                        configFile: false,

                        // The configuration for compilation
                        presets: [
                            ['@babel/preset-env',
                             { useBuiltIns: 'usage',
                               corejs: 3}],
                            '@babel/preset-react',
                            '@babel/preset-flow',
                            "@babel/preset-typescript"
                        ],
                        plugins: [
                            '@babel/plugin-proposal-class-properties',
                            '@babel/plugin-proposal-object-rest-spread'
                        ],
                    },
                },
            },

        ],
    },
    resolve: {
        alias: {
            'react-native$': require.resolve('react-native-web'),
        },
        // If you're working on a multi-platform React Native app, web-specific
        // module implementations should be written in files using the extension
        // `.web.js`.
        extensions: [ '.web.js', '.js' ]
    },

    // Development server config
    devServer: {
        contentBase: [path.join(__dirname, 'public')],
        historyApiFallback: true,
    },
};
