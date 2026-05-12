# GitHub Actions & Netlify Deployment Setup

This guide explains how to set up GitHub Actions workflows for automated deployment of the frontend to Netlify and backend Docker image builds.

## Prerequisites

- GitHub repository
- Netlify account
- Docker Hub account (optional, for publishing images)
- GitHub Container Registry (GHCR) enabled

## Frontend Deployment (Netlify)

### Step 1: Create Netlify Site

1. Go to [netlify.com](https://netlify.com) and sign in
2. Click "Add new site" → "Import an existing project"
3. Connect your GitHub repository
4. Configure build settings:
   - Build command: `npm ci && npm run build`
   - Publish directory: `frontend/dist`
   - Click "Deploy"

### Step 2: Generate Netlify Tokens

1. Go to Netlify Settings → [Personal access tokens](https://app.netlify.com/user/applications#personal-access-tokens)
2. Click "New access token"
3. Copy the token
4. Get your Site ID from: Site settings → "General" → "Site details" → "Site ID"

### Step 3: Add GitHub Secrets

Add these secrets to your repository (Settings → Secrets and variables → Actions):

```
NETLIFY_AUTH_TOKEN=<your-netlify-auth-token>
NETLIFY_SITE_ID=<your-netlify-site-id>
```

### Step 4: Update Netlify Configuration

The `frontend/netlify.toml` file is already configured with:
- Build command
- Redirects for SPA routing
- Cache headers
- Environment-specific builds

## Backend Deployment (Render.com)

### Step 1: Create Render Account
1. Go to [render.com](https://render.com) and sign up
2. Connect your GitHub account

### Step 2: Create Web Service
1. Click "New +" → "Web Service"
2. Connect your GitHub repository
3. Configure:
   - Branch: `main`
   - Environment variables (add in Advanced):
     ```
     SPRING_PROFILES_ACTIVE=production
     JAVA_OPTS=-Xmx512m
     ```
4. Click "Create Web Service"

### Step 3: Get Deploy Hook
1. Go to Service Settings → "Deploy Hook"
2. Copy the Deploy Hook URL

### Step 4: Add GitHub Secret
Add to GitHub repository secrets:
```
RENDER_DEPLOY_HOOK=https://api.render.com/deploy/srv-xxxxxxxxxxxxx?key=xxxxxxxx
```

## Workflow Triggers

### Frontend Workflow
Triggers on:
- Push to `main` or `develop` with changes in `frontend/` directory
- Manual trigger via "Run workflow" button

### Backend Workflow
Triggers on:
- Push to `main` or `develop` with changes in `backend/` or `Dockerfile`
- Builds Docker image and runs tests
- Manual trigger via "Run workflow" button

## GitHub Actions Secrets Summary

| Secret | Purpose | Source |
|--------|---------|--------|
| `NETLIFY_AUTH_TOKEN` | Netlify authentication | Netlify dashboard |
| `NETLIFY_SITE_ID` | Netlify site identifier | Netlify site settings |
| `RENDER_DEPLOY_HOOK` | Render deployment trigger | Render service settings |

## Viewing Deployments

### Frontend Deployments
- Netlify Dashboard: https://app.netlify.com
- GitHub Actions: Repository → Actions → "Deploy Frontend to Netlify"

### Backend Deployments
- Render Dashboard: https://dashboard.render.com
- GitHub Actions: Repository → Actions → "Build and Push Backend Docker Image"
- Service URL: https://inventory-movement-backend.onrender.com (after deployment)

## Environment Variables

### Frontend (Netlify)
Add in Netlify Site settings → Build & deploy → Environment:
```
VITE_API_URL=https://inventory-movement-backend.onrender.com
```

### Backend (Render)
Add in Render Service settings → Environment:
```
SPRING_PROFILES_ACTIVE=production
JAVA_OPTS=-Xmx512m
```

## Running Locally

### Using Docker Compose:
```bash
docker-compose up -d
# Frontend: http://localhost:5173
# Backend: http://localhost:8080
```

### Manual development:
```bash
# Terminal 1: Start backend
cd backend
mvn spring-boot:run

# Terminal 2: Start frontend
cd frontend
npm install
npm run dev
```

## Troubleshooting

**Frontend deployment fails:**
- Check Netlify build logs in Netlify dashboard
- Verify Node.js version compatibility
- Ensure `frontend/dist` directory exists after build

**Backend deployment to Render fails:**
- Check Render service logs: Service → Logs tab
- Verify Deploy Hook URL is correct in GitHub secrets
- Ensure Docker image builds successfully (check GitHub Actions logs)
- Check Spring Boot health endpoint: `/actuator/health`

**Service won't start on Render:**
- Free tier has 512MB RAM limit - verify app fits
- Check environment variables are set correctly
- Review Spring Boot startup logs in Render

## Next Steps

1. Set up GitHub secrets (see above)
2. Push changes to `main` branch
3. GitHub Actions will automatically trigger
4. Monitor deployments in GitHub Actions and respective dashboards
