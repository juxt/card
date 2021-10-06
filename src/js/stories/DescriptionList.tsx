import { User } from "../types";

const renderField = (field: string, value: string) => {
  return (
    <div key={field} className="sm:col-span-1">
      <dt className="text-sm font-medium text-gray-500">{field}</dt>
      <dd className="mt-1 text-sm text-gray-900">{value || "—"}</dd>
    </div>
  );
};

const DEFAULT_FIELDS = {
  phone: "",
  email: "",
  title: "",
  team: "",
  location: "",
  sits: "",
  salary: "",
  birthday: "",
};

export function DescriptionList({
  profile,
  isLoading,
}: {
  profile?: User;
  isLoading: boolean;
}) {
  const fields = isLoading ? DEFAULT_FIELDS : profile?.fields;
  return (
    <div className="mt-6 max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
      <dl className="grid grid-cols-1 gap-x-4 gap-y-8 sm:grid-cols-2">
        {fields &&
          Object.keys(fields).map((field) => {
            return renderField(field, fields[field] || "");
          })}
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
