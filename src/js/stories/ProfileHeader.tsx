import { MailIcon, PhoneIcon } from "@heroicons/react/solid";
import { User } from "../types";

export type ProfileHeaderProps = {
  profile?: User;
  isLoading: boolean;
};

export function ProfileHeader({ profile, isLoading }: ProfileHeaderProps) {
  const profileImageClass =
    "h-24 w-24 rounded-full ring-4 ring-white sm:h-32 sm:w-32";
  const coverImageClass = "h-32 w-full object-cover lg:h-48";
  return (
    <div className={`${isLoading && "animate-pulse "}`}>
      <div>
        {isLoading ? (
          <div className={`${coverImageClass} bg-gray-200`}></div>
        ) : (
          <img
            className={coverImageClass}
            src={profile!.coverImageUrl}
            alt={profile!.name + "'s Cover Image"}
          />
        )}
      </div>
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="-mt-12 sm:-mt-16 sm:flex sm:items-end sm:space-x-5">
          <div className="flex">
            {isLoading ? (
              <div className={`${profileImageClass} bg-gray-400`}></div>
            ) : (
              <img
                className={profileImageClass}
                src={profile!.imageUrl}
                alt={profile!.name + "'s Profile Image"}
              />
            )}
          </div>
          <div className="mt-6 sm:flex-1 sm:min-w-0 sm:flex sm:items-center sm:justify-end sm:space-x-6 sm:pb-1">
            <div className="sm:hidden 2xl:block mt-6 min-w-0 flex-1">
              <h1 className="text-2xl font-bold text-gray-900 truncate">
                {profile?.name || "Loading..."}
              </h1>
            </div>
            <div className="mt-6 flex flex-col justify-stretch space-y-3 sm:flex-row sm:space-y-0 sm:space-x-4">
              <button
                type="button"
                className="inline-flex justify-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-pink-500"
              >
                <MailIcon
                  className="-ml-1 mr-2 h-5 w-5 text-gray-400"
                  aria-hidden="true"
                />
                <span>Message</span>
              </button>
              <button
                type="button"
                className="inline-flex justify-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-pink-500"
              >
                <PhoneIcon
                  className="-ml-1 mr-2 h-5 w-5 text-gray-400"
                  aria-hidden="true"
                />
                <span>Call</span>
              </button>
            </div>
          </div>
        </div>
        <div className="hidden sm:block 2xl:hidden mt-6 min-w-0 flex-1">
          <h1 className="text-2xl font-bold text-gray-900 truncate">
            {profile?.name || "Loading..."}
          </h1>
        </div>
      </div>
    </div>
  );
}
