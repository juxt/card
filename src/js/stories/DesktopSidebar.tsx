import { SidebarFooter } from "./SidebarFooter";
import { SidebarNav } from "./SidebarNav";
import { SidebarProps } from "../types";

export function Sidebar({ user, ...props }: SidebarProps) {
  return (
    <div className="flex-1 flex flex-col min-h-0 border-r border-gray-200 bg-white max-w-sm">
      <div className="flex-1 flex flex-col pt-5 pb-4 overflow-y-auto">
        <SidebarNav {...props} />
      </div>
      <SidebarFooter user={user}></SidebarFooter>
    </div>
  );
}
