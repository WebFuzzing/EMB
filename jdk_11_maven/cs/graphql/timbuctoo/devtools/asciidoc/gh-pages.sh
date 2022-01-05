#!/bin/sh

#set -o xtrace

#Don't allow this command if your working dir or index has changes (we're going to add files and reset the index)
#we could also work around this using GIT_INDEX_FILE, but this check additionally ensures that the gh-pages result is really derived from the current HEAD
if git status --porcelain | grep -q .; then
  echo "Please make sure the working dir and index are clean"
  exit 1
fi

#add output index (use -f because output is ignored)
git add -f documentation/output
#create a tree of it
treehash=$(git write-tree --prefix=documentation/output/)
# clear index again
git reset HEAD 

# get the current gh-pages HEAD
curghpages=$(git rev-parse --verify gh-pages)
if [ -z "$curghpages" ]; then
  echo "no gh-pages found"
  exit 1
fi
# create a new commit that points to the prev gh-pages commit and the current HEAD. Give it a description that names the current branch
commithash=$(git commit-tree $treehash -p $curghpages -p $(git rev-parse HEAD) -m "Documentation from $(git rev-parse --abbrev-ref HEAD)")

if [ -z "$commithash" ]; then
  echo "commit not created successfully"
  exit 1
fi

# move gh-pages to the new branch
git branch -f gh-pages $commithash

echo "created commit $commithash, pushing to origin"

git push origin gh-pages
