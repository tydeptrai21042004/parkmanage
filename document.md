Here's a clear, step-by-step guide to connecting your local Git project to GitHub and pushing your code:

---

## Step 1: Prepare Your Local Project

1. **Install Git** (if not already).
   Then, in your project directory:

   ```bash
   cd path/to/your/project
   git init
   ```

   This initializes Git in your folder. ([GitHub Docs][1])

2. **Add files** to staging and commit them:

   ```bash
   git add .
   git commit -m "Initial commit"
   ```

   `git add .` stages all files; `git commit` snapshots your changes. ([Medium][2], [product.hubspot.com][3])

---

## Step 2: Create a Remote Repository on GitHub

1. **Create a new repository** on GitHub:

   * Go to your GitHub account and click **New repository (+)**.
   * Name the repo (e.g., `my-project`).
   * **Important**: Do NOT initialize with a README, `.gitignore`, or license. It’s easier to push your existing project when the repo is empty. ([Medium][2], [GitHub Docs][1])

2. After creating it, click **Code** and copy the repository URL (HTTPS or SSH).

---

## Step 3: Link Local to GitHub

1. Back in your terminal, set up the remote origin:

   ```bash
   git remote add origin <your-repo-URL>
   ```

   Replace `<your-repo-URL>` with the one you copied. ([Medium][2], [TheServerSide][4], [git-tower.com][5])

2. Verify the remote:

   ```bash
   git remote -v
   ```

   This shows the URL origin is pointing to. ([GitHub Docs][1])

---

## Step 4: Push Your Code to GitHub

1. Push your local branch (commonly `main`, or `master`) to GitHub:

   ```bash
   git push -u origin main
   ```

   * `-u` sets this branch as the default for future pushes.
   * If your branch is named `master`, adjust accordingly. ([GitHub Docs][1], [Medium][2], [git-tower.com][5])

2. If pushing to a non-empty repo or you're syncing with existing history, you might need to force push:

   ```bash
   git push -u -f origin main
   ```

   Use with caution—this can overwrite remote content. ([TheServerSide][4])

---

## Optional: Using GitHub CLI Instead of the Web & Git

If you'd prefer to handle everything via terminal:

1. Use GitHub CLI (`gh`) to create and push, all in one go:

   ```bash
   gh repo create --source=. --public --remote=origin --push
   ```

   This creates a GitHub repo, links it as `origin`, and pushes your code. ([GitHub Docs][1])

---

## Recap: Full Command Flow

```bash
cd path/to/your/project
git init
git add .
git commit -m "Initial commit"

# Option A: If creating repo manually on GitHub
git remote add origin https://github.com/your-username/your-repo.git
git push -u origin main

# Option B: Using GitHub CLI
gh repo create my-repo-name --source=. --public --remote=origin --push
```

---

## Tips & Troubleshooting

* **Branch names**: Newer Git defaults to `main`; older setups use `master`. Use `git branch` or `git status` to check. ([GitHub][6], [Wikipedia][7])
* **Clone & copy approach**: If your GitHub repo has existing files (like README), you can instead clone it locally and copy your project inside before committing. ([TheServerSide][4])
* **GitHub Desktop or GUIs**: These tools offer point-and-click alternatives if you're not comfortable with the terminal. ([GitHub Docs][1], [git-tower.com][5])
* **Authentication**: Use HTTPS + credential caching or SSH keys to authenticate. SSH offers a smoother experience if pushing often. ([GitHub Docs][8])

---

### Let Me Know!

Want help with a specific step—like setting up SSH keys, resolving errors, or using VS Code for Git integration? Just say the word and I’d be happy to assist!

[1]: https://docs.github.com/en/migrations/importing-source-code/using-the-command-line-to-import-source-code/adding-locally-hosted-code-to-github?utm_source=chatgpt.com "Adding locally hosted code to GitHub"
[2]: https://medium.com/%40ravipatel.it/how-to-push-an-existing-local-project-to-github-a-beginners-guide-ede021feb717?utm_source=chatgpt.com "How to Push an Existing Local Project to GitHub"
[3]: https://product.hubspot.com/blog/git-and-github-tutorial-for-beginners?utm_source=chatgpt.com "An Intro to Git and GitHub for Beginners (Tutorial)"
[4]: https://www.theserverside.com/blog/Coffee-Talk-Java-News-Stories-and-Opinions/How-to-push-an-existing-project-to-GitHub?utm_source=chatgpt.com "How to git push an existing project to GitHub"
[5]: https://www.git-tower.com/learn/git/faq/push-to-github?utm_source=chatgpt.com "How to Push to GitHub | Learn Version Control with Git"
[6]: https://github.com/git-guides/git-push?utm_source=chatgpt.com "Git Guides - git push"
[7]: https://en.wikipedia.org/wiki/Git?utm_source=chatgpt.com "Git"
[8]: https://docs.github.com/en/get-started/git-basics/set-up-git?utm_source=chatgpt.com "Set up Git"
