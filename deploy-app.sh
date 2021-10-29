rm -rf build
yarn release
site put-static-site -d build -p _card
