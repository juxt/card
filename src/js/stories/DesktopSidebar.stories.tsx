import { Sidebar } from "./DesktopSidebar";
import { Meta, Story } from "@storybook/react";
import { SidebarProps } from "../types";
import { MOCK_NAVIGATION, MOCK_USER } from "../utils";

export default {
  title: "People/DesktopSidebar",
  component: Sidebar,
} as Meta;

const Template: Story<SidebarProps> = (args) => <Sidebar {...args} />;

export const DesktopSidebar = Template.bind({});
DesktopSidebar.args = {
  navigation: MOCK_NAVIGATION,
  user: MOCK_USER,
};

export const DesktopSidebarNoAvatar = Template.bind({});
DesktopSidebarNoAvatar.args = {
  ...DesktopSidebar.args,
  user: { ...MOCK_USER, imageUrl: undefined },
};
