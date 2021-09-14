import { classNames } from "../utils";
import { NavigationItem } from "../types";

export interface SidebarNavProps {
  navigation: NavigationItem[];
  secondaryNavigation: NavigationItem[];
  // selectedIndex: number;
  // onSelect: (index: number) => void;
}

export function SidebarNav(props: SidebarNavProps) {
  return (
    <nav aria-label="Sidebar" className="mt-5">
      <div className="px-2 space-y-1">
        {props.navigation.map((item) => (
          <a
            key={item.name}
            href={item.href}
            className={classNames(
              item.current
                ? "bg-gray-100 text-gray-900"
                : "text-gray-600 hover:bg-gray-50 hover:text-gray-900",
              "group flex items-center px-2 py-2 text-base font-medium rounded-md"
            )}
            aria-current={item.current ? "page" : undefined}
          >
            {item.icon && (
              <item.icon
                className={classNames(
                  item.current
                    ? "text-gray-500"
                    : "text-gray-400 group-hover:text-gray-500",
                  "mr-4 h-6 w-6"
                )}
                aria-hidden="true"
              />
            )}
            {item.name}
          </a>
        ))}
      </div>
      {props.secondaryNavigation && (
        <>
          <hr className="border-t border-gray-200 my-5" aria-hidden="true" />
          <div className="px-2 space-y-1">
            {props.secondaryNavigation.map((item) => (
              <a
                key={item.name}
                href={item.href}
                className="text-gray-600 hover:bg-gray-50 hover:text-gray-900 group flex items-center px-2 py-2 text-base font-medium rounded-md"
              >
                {item.icon && (
                  <item.icon
                    className="text-gray-400 group-hover:text-gray-500 mr-4 flex-shrink-0 h-6 w-6"
                    aria-hidden="true"
                  />
                )}
                {item.name}
              </a>
            ))}
          </div>
        </>
      )}
    </nav>
  );
}
