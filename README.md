 Command line instructions
`
Create a new repository
`
git clone https://git-teaching.cs.bham.ac.uk/mod-team-proj-2017/knightlore.git
cd knightlore
touch README.md
git add README.md
git commit -m "add README"
git push -u origin master
`
Existing folder
`
cd existing_folder
git init
git remote add origin https://git-teaching.cs.bham.ac.uk/mod-team-proj-2017/knightlore.git
git add .
git commit -m "Initial commit"
git push -u origin master
`
Existing Git repository
`
cd existing_repo
git remote rename origin old-origin
git remote add origin https://git-teaching.cs.bham.ac.uk/mod-team-proj-2017/knightlore.git
git push -u origin --all
`