import { ChevronLeftIcon } from "@heroicons/react/solid";
import { NavigationItem } from "../types";

export default function ({ page }: { page: NavigationItem }) {
  const { name, href } = page;
  return (
    <nav
      className="flex items-start px-4 py-3 sm:px-6 lg:px-8 xl:hidden"
      aria-label="Breadcrumb"
    >
      <a
        href={href}
        className="inline-flex items-center space-x-3 text-sm font-medium text-gray-900"
      >
        <ChevronLeftIcon
          className="-ml-2 h-5 w-5 text-gray-400"
          aria-hidden="true"
        />
        <span>{name}</span>
      </a>
    </nav>
  );
}
