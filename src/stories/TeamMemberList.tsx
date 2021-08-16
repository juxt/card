export function TeamMemberList(props) {
  return (
    <div className="mt-8 max-w-5xl mx-auto px-4 pb-12 sm:px-6 lg:px-8">
      <h2 className="text-sm font-medium text-gray-500">Team members</h2>
      <div className="mt-1 grid grid-cols-1 gap-4 sm:grid-cols-2">
        {props?.team && props.team.map((person) => (
          <div
            key={person.handle}
            className="relative rounded-lg border border-gray-300 bg-white px-6 py-5 shadow-sm flex items-center space-x-3 hover:border-gray-400 focus-within:ring-2 focus-within:ring-offset-2 focus-within:ring-pink-500"
          >
            <div className="flex-shrink-0">
              <img
                className="h-10 w-10 rounded-full"
                src={person.imageUrl}
                alt="" />
            </div>
            <div className="flex-1 min-w-0">
              <a href="#" className="focus:outline-none">
                <span className="absolute inset-0" aria-hidden="true" />
                <p className="text-sm font-medium text-gray-900">
                  {person.name}
                </p>
                <p className="text-sm text-gray-500 truncate">{person.role}</p>
              </a>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
