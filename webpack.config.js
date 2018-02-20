var path = require('path');
var webpack = require('webpack');

module.exports = {
    entry: {
		app: './src/main/js/app.js',
	},
    devtool: 'source-map',
    cache: true,
    output: {
        path: __dirname,
		filename: "./src/main/resources/static/js/built/[name].bundle.js",
    },
	plugins: [
		new webpack.LoaderOptionsPlugin({
			debug: true
		})
	],
    module: {
        loaders: [
            {
                test: path.join(__dirname, '.'),
                exclude: /(node_modules)/,
                loader: 'babel-loader',
                query: {
                    cacheDirectory: true,
                    presets: ['es2015', 'react', 'stage-2']
                }
            },
            {test: /\.css$/, loader: 'style-loader!css-loader'},
            {test: /\.scss$/, loader: 'style-loader!css-loader!sass-loader'}
        ]
    }
};
