import { Sidebar } from "./DesktopSidebar";
import { Meta, Story } from "@storybook/react";
import {
  CalendarIcon,
  ChartBarIcon,
  FolderIcon,
  HomeIcon,
  InboxIcon,
  UsersIcon,
} from "@heroicons/react/outline";
import { SidebarProps } from "../types";
import { MOCK_USER } from "../utils";

export default {
  title: "People/DesktopSidebar",
  component: Sidebar,
} as Meta;

const Template: Story<SidebarProps> = (args) => <Sidebar {...args} />;

export const DesktopSidebar = Template.bind({});
DesktopSidebar.args = {
  navigation: [
    { name: "Dashboard", icon: HomeIcon, current: true },
    { name: "Team", icon: UsersIcon, current: false },
    { name: "Projects", icon: FolderIcon, current: false },
    { name: "Calendar", icon: CalendarIcon, current: false },
    { name: "Documents", icon: InboxIcon, current: false },
    { name: "Reports", icon: ChartBarIcon, current: false },
  ],
  user: MOCK_USER,
};

export const DesktopSidebarNoAvatar = Template.bind({});
DesktopSidebarNoAvatar.args = {
  ...DesktopSidebar.args,
  user: { ...MOCK_USER, imageUrl: undefined },
};
