import { DirectoryListProps, DirectoryList } from "./DirectoryList";
import { Meta, Story } from "@storybook/react";
import { MOCK_DIRECTORY } from "../utils";

export default {
  title: "People/DirectoryList",
  component: DirectoryList,
  parameters: { actions: { argTypesRegex: "^on.*" } },
} as Meta;

const Template: Story<DirectoryListProps> = (args) => (
  <DirectoryList {...args} />
);
export const DirectoryListStory = Template.bind({});
DirectoryListStory.args = {
  directory: MOCK_DIRECTORY,
};
