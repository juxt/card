import { User } from "../types";

export function DescriptionList({ profile }: { profile: User }) {
  return (
    <div className="mt-6 max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
      <dl className="grid grid-cols-1 gap-x-4 gap-y-8 sm:grid-cols-2">
        {profile?.fields &&
          Object.keys(profile.fields).map((field) => (
            <div key={field} className="sm:col-span-1">
              <dt className="text-sm font-medium text-gray-500">{field}</dt>
              <dd className="mt-1 text-sm text-gray-900">
                {profile?.fields && profile.fields[field]}
              </dd>
            </div>
          ))}
        {profile?.about && (
          <div className="sm:col-span-2">
            <dt className="text-sm font-medium text-gray-500">About</dt>
            <dd
              className="mt-1 max-w-prose text-sm text-gray-900 space-y-5"
              dangerouslySetInnerHTML={{
                __html: profile.about,
              }}
            />
          </div>
        )}
      </dl>
    </div>
  );
}
