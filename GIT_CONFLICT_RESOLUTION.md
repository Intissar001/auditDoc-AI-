# Guide de Résolution des Conflits Git

## 🔄 Scénario 1 : Merger main dans votre branche

```bash
# 1. Sauvegarder votre travail actuel
git add .
git commit -m "Sauvegarde avant merge"

# 2. Mettre à jour main
git checkout main
git pull origin main

# 3. Revenir sur votre branche
git checkout feature/settings-page-improvements

# 4. Merger main
git merge main

# 5. Si conflits, résoudre puis :
git add .
git commit -m "Merge main - conflicts resolved"
git push origin feature/settings-page-improvements
```

## 🔄 Scénario 2 : Rebaser votre branche sur main

```bash
# 1. Sauvegarder votre travail
git add .
git commit -m "Sauvegarde avant rebase"

# 2. Mettre à jour main
git checkout main
git pull origin main

# 3. Revenir sur votre branche
git checkout feature/settings-page-improvements

# 4. Rebaser
git rebase main

# 5. Si conflits, résoudre puis :
git add .
git rebase --continue

# 6. Si plusieurs conflits, répéter 5 jusqu'à la fin
# 7. Pousser (force push nécessaire après rebase)
git push origin feature/settings-page-improvements --force-with-lease
```

## 🔄 Scénario 3 : Annuler un merge en cours

```bash
# Si vous voulez annuler un merge qui a des conflits
git merge --abort
```

## 🔄 Scénario 4 : Voir les différences avant de merger

```bash
# Voir ce qui va changer
git diff main...feature/settings-page-improvements

# Voir les fichiers qui seront affectés
git diff --name-only main...feature/settings-page-improvements
```

## 📝 Commandes de diagnostic

```bash
# Voir l'historique des commits
git log --oneline --graph --all

# Voir les fichiers modifiés
git status

# Voir les différences dans un fichier spécifique
git diff <nom-du-fichier>

# Voir qui a modifié quoi
git blame <nom-du-fichier>
```

## ⚠️ Commandes d'urgence

```bash
# Annuler tous les changements non commités (DANGEREUX!)
git reset --hard HEAD

# Annuler les changements d'un fichier spécifique
git restore <nom-du-fichier>

# Voir les conflits dans un fichier
git diff --check
```

## 🎯 Workflow recommandé pour votre projet

```bash
# 1. Toujours commiter votre travail avant de merger
git add .
git commit -m "Description de vos changements"

# 2. Mettre à jour main
git fetch origin
git checkout main
git pull origin main

# 3. Revenir sur votre branche et merger
git checkout feature/settings-page-improvements
git merge main

# 4. Résoudre les conflits si nécessaire
# (éditer les fichiers, supprimer les marqueurs <<<<<<< ======= >>>>>>>)

# 5. Finaliser
git add .
git commit -m "Merge main - conflicts resolved"
git push origin feature/settings-page-improvements
```

