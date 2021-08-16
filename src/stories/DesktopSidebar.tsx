import { SidebarFooter } from "./SidebarFooter";
import { SidebarNav } from "./SidebarNav";

export function Sidebar({ user, ...props }) {
  return (
    <div className="flex-1 flex flex-col min-h-0 border-r border-gray-200 bg-white max-w-sm">
      <div className="flex-1 flex flex-col pt-5 pb-4 overflow-y-auto">
        <div className="flex items-center flex-shrink-0 px-4">
          <img
            className="h-8 w-auto"
            src="https://tailwindui.com/img/logos/workflow-logo-indigo-600-mark-gray-800-text.svg"
            alt="Card"
          />
        </div>
        <SidebarNav {...props} />
      </div>
      <SidebarFooter user={user}></SidebarFooter>
    </div>
  );
}
