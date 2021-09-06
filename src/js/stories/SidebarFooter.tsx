import { User } from "../types";

export function SidebarFooter(props: { user: User }) {
  return (
    <div className="flex-shrink-0 flex border-t border-gray-200 p-4">
      <a href="#" className="flex-shrink-0 w-full group block">
        <div className="flex items-center">
          <div>
            <img
              className="inline-block h-9 w-9 rounded-full"
              src={
                props.user?.imageUrl ??
                "https://eu.ui-avatars.com/api/?name=" + props.user?.name
              }
              alt={props.user?.name + "'s imageUrl"}
            />
          </div>
          <div className="ml-3">
            <p className="text-sm font-medium text-gray-700 group-hover:text-gray-900">
              {props.user?.name}
            </p>
            <p className="text-xs font-medium text-gray-500 group-hover:text-gray-700">
              View profile
            </p>
          </div>
        </div>
      </a>
    </div>
  );
}
