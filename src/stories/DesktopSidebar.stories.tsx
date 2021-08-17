import { Sidebar } from "./DesktopSidebar";
import {
  CalendarIcon,
  ChartBarIcon,
  FolderIcon,
  HomeIcon,
  InboxIcon,
  UsersIcon,
} from "@heroicons/react/outline";

export default {
  title: "People/DesktopSidebar",
  component: Sidebar,
};

const Template = (args) => <Sidebar {...args} />;

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
  user: {
    name: "Alex Davis",
    imageUrl: "https://ca.slack-edge.com/T02AJV0T3-U7KDWJTT6-500d11650fe2-512",
  },
};

export const DesktopSidebarNoAvatar = Template.bind({});
DesktopSidebarNoAvatar.args = {
  ...DesktopSidebar.args,
  user: {
    name: "Alex Davis",
  },
};
