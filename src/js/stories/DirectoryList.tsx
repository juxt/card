import { FilterIcon, SearchIcon } from "@heroicons/react/solid";
import { useState } from "react";
import { Directory as TDirectory } from "../types";
import { Link } from "react-router-dom";
import { MOCK_USER } from "../utils";
export type DirectoryListProps = {
  directory?: TDirectory;
  isLoading: boolean;
};

type DirectoryProps = {
  letter: string;
  directory?: TDirectory;
  isLoading?: boolean;
  searchText?: string;
};

const groupedPeople = ({
  letter,
  directory,
  searchText = "",
}: DirectoryProps) => {
  return directory![letter].filter(
    (person) =>
      // Filter people in search results
      person.name.toLowerCase().indexOf(searchText.toLowerCase()) !== -1
  );
};

const Directory = (props: DirectoryProps) => {
  const { letter } = props;
  const directoryItemClass =
    "relative px-6 py-5 flex items-center space-x-3 hover:bg-gray-50 focus-within:ring-2 focus-within:ring-inset focus-within:ring-pink-500";
  return (
    <div key={letter} className="relative">
      <div className="z-10 sticky top-0 border-t border-b border-gray-200 bg-gray-50 px-6 py-1 text-sm font-medium text-gray-500">
        <h3>{letter}</h3>
      </div>
      <ul className="relative z-0 divide-y divide-gray-200">
        {props.isLoading ? (
          <div className={`animate-pulse ${directoryItemClass}`}>
            <div className="flex-shrink-0">
              <div className="h-10 w-10 bg-gray-700 rounded-full" />
            </div>
            <div className="flex-1 space-y-2 min-w-0">
              <div className="h-4 bg-gray-700 rounded"></div>
              <div className="h-4 bg-gray-700 rounded w-5/6"></div>
            </div>
          </div>
        ) : (
          groupedPeople(props).map((person) => (
            <li key={person.id}>
              <div className={directoryItemClass}>
                <div className="flex-shrink-0">
                  <img
                    className="h-10 w-10 rounded-full"
                    src={person.imageUrl}
                    alt=""
                  />
                </div>
                <div className="flex-1 min-w-0">
                  <Link
                    to={`/people?selected=${person.id}`}
                    className="focus:outline-none"
                  >
                    {/* Extend touch target to entire panel */}
                    <span className="absolute inset-0" aria-hidden="true" />
                    <p className="text-sm font-medium text-gray-900">
                      {person.name}
                    </p>
                    <p className="text-sm text-gray-500 truncate">
                      {person.role}
                    </p>
                  </Link>
                </div>
              </div>
            </li>
          ))
        )}
      </ul>
    </div>
  );
};

const alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");

export function DirectoryList({ directory, isLoading }: DirectoryListProps) {
  const [searchText, setSearchText] = useState("");
  return (
    <aside className="hidden xl:order-first xl:flex xl:flex-col flex-shrink-0 w-96 border-r border-gray-200">
      <div className="px-6 pt-6 pb-4">
        <h2 className="text-lg font-medium text-gray-900">Directory</h2>
        <p className="mt-1 text-sm text-gray-600">
          {isLoading
            ? "Loading..."
            : `Search directory of ${
                Object.values(directory!).length
              } employees`}
        </p>
        <form className="mt-6 flex space-x-4">
          <div className="flex-1 min-w-0">
            <label htmlFor="search" className="sr-only">
              Search
            </label>
            <div className="relative rounded-md shadow-sm">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <SearchIcon
                  className="h-5 w-5 text-gray-400"
                  aria-hidden="true"
                />
              </div>
              <input
                type="search"
                name="search"
                id="search"
                onChange={(e) => setSearchText(e.target.value)}
                className="focus:ring-pink-500 focus:border-pink-500 block w-full pl-10 sm:text-sm border-gray-300 rounded-md"
                placeholder="Search"
              />
            </div>
          </div>
          <button
            type="submit"
            className="inline-flex justify-center px-3.5 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-pink-500"
          >
            <FilterIcon className="h-5 w-5 text-gray-400" aria-hidden="true" />
            <span className="sr-only">Search</span>
          </button>
        </form>
      </div>
      <nav className="flex-1 min-h-0 overflow-y-auto" aria-label="Directory">
        {isLoading
          ? alphabet.map((letter) => (
              <Directory key={letter} letter={letter} isLoading />
            ))
          : Object.keys(directory!)
              .sort()
              .filter(
                (letter) =>
                  groupedPeople({ letter, searchText, directory })?.length > 0
              )
              .map((letter) => (
                <Directory
                  key={letter}
                  letter={letter}
                  directory={directory!}
                  searchText={searchText}
                />
              ))}
      </nav>
    </aside>
  );
}
