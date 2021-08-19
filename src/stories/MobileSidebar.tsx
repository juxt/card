import { Transition, Dialog } from "@headlessui/react";
import { XIcon } from "@heroicons/react/outline";
import { Fragment } from "react";

import { SidebarFooter } from "./SidebarFooter";
import { SidebarNav } from "./SidebarNav";
import { NavigationItem, User } from "./types";

export type MobileSidebarProps = {
  sidebarOpen: boolean;
  setSidebarOpen: (open: boolean) => void;
  user: User;
  navigation: NavigationItem[];
  secondaryNavigation: NavigationItem[];
};

export const MobileSidebar = ({
  sidebarOpen,
  setSidebarOpen,
  user,
  ...props
}: MobileSidebarProps) => (
  <Transition.Root show={sidebarOpen} as={Fragment}>
    <Dialog
      as="div"
      static
      className="fixed inset-0 flex z-40 lg:hidden"
      open={sidebarOpen}
      onClose={setSidebarOpen}
    >
      <Transition.Child
        as={Fragment}
        enter="transition-opacity ease-linear duration-300"
        enterFrom="opacity-0"
        enterTo="opacity-100"
        leave="transition-opacity ease-linear duration-300"
        leaveFrom="opacity-100"
        leaveTo="opacity-0"
      >
        <Dialog.Overlay className="fixed inset-0 bg-gray-600 bg-opacity-75" />
      </Transition.Child>
      <Transition.Child
        as={Fragment}
        enter="transition ease-in-out duration-300 transform"
        enterFrom="-translate-x-full"
        enterTo="translate-x-0"
        leave="transition ease-in-out duration-300 transform"
        leaveFrom="translate-x-0"
        leaveTo="-translate-x-full"
      >
        <div className="relative flex-1 flex flex-col max-w-xs w-full bg-white focus:outline-none">
          <Transition.Child
            as={Fragment}
            enter="ease-in-out duration-300"
            enterFrom="opacity-0"
            enterTo="opacity-100"
            leave="ease-in-out duration-300"
            leaveFrom="opacity-100"
            leaveTo="opacity-0"
          >
            <div className="absolute top-0 right-0 -mr-12 pt-2">
              <button
                type="button"
                className="ml-1 flex items-center justify-center h-10 w-10 rounded-full focus:outline-none focus:ring-2 focus:ring-inset focus:ring-white"
                onClick={() => setSidebarOpen(false)}
              >
                <span className="sr-only">Close sidebar</span>
                <XIcon className="h-6 w-6 text-white" aria-hidden="true" />
              </button>
            </div>
          </Transition.Child>
          <div className="flex-1 h-0 pt-5 pb-4 overflow-y-auto">
            <div className="flex-shrink-0 flex items-center px-4">
              <img
                className="h-8 w-auto"
                src="https://tailwindui.com/img/logos/workflow-logo-pink-500-mark-gray-900-text.svg"
                alt="Workflow"
              />
            </div>
            <SidebarNav {...props} />
          </div>
          <SidebarFooter user={user} />
        </div>
      </Transition.Child>
      <div className="flex-shrink-0 w-14" aria-hidden="true">
        {/* Force sidebar to shrink to fit close icon */}
      </div>
    </Dialog>
  </Transition.Root>
);
