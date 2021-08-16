const path = require("path");

module.exports = {
  typescript: {
    check: false,
    checkOptions: {},
    reactDocgen: false,
  },
  reactOptions: {
    fastRefresh: true,
  },
  stories: ["../src/**/*.stories.tsx"],
  addons: [
    "@storybook/addon-essentials",
    {
      name: "@storybook/addon-postcss",
      options: {
        postcssLoaderOptions: {
          implementation: require("postcss"),
        },
      },
    },
  ],
};
