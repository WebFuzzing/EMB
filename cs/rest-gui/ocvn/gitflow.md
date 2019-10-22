# OCVN (Open Contracting Vietnam)

For Developers - guidelines to correctly use the gitflow workflow. 

[Very nice explanation of the gitflow workflow] (https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow), in case you have not read it already

## Guidelines for developing new features:

- each new feature is branched from **develop**
- the feature branch name has the format **feature/JIRA-ID** Example: feature/OCVN-63
- once the feature is done, you have to mark the JIRA ticket as RESOLVED and assign it to the project maintener, then go to github select your feature branch + develop branch and [start a pull request] (https://help.github.com/articles/creating-a-pull-request/)
- if you get stuck while doing the feature, you have to go to github, select your feature branch + develop branch and [start a pull request] (https://help.github.com/articles/creating-a-pull-request/)
- pull requests [are closed by the project maintainer](https://www.atlassian.com/git/tutorials/making-a-pull-request/how-it-works)
- feature branches are deleted after successful pull request is closed, by the project maintainer


## Guidelines for creating new releases:

- each new release is branched from **develop**
- the release branch name has the format **release/version** Example: release/0.2
- once the release branch is created, the staging is switched to that branch and testing can begin
- if a bug is discovered during the testing, it is committed directly on the release branch
- when the release branch becomes stable enough it has to be:
  - merged back into **develop**
  - merged into **master**
  - master is tagged with **version**: Example: 0.2
  - release branched is deleted
  

##Guidelines for applying hotfixes:

- if a bug is discovered, a new branch has to be created out of **master** branch
- this branch is called a hotfix branch and it has the format **fix/JIRA-ID** Example fix/OCVN-62
- once the hotfix is done, you have to mark the JIRA ticket as RESOLVED and assign it to the project maintener, then go to github, select master and the fix branch [start a pull request] (https://help.github.com/articles/creating-a-pull-request/)
- pull requests [are closed by the project maintainer](https://www.atlassian.com/git/tutorials/making-a-pull-request/how-it-works)
- the project maintainer will also merge the fix branch to the **develop** branch
- the hotfix branch is deleted
  
