# Render.com Deployment Guide

Complete guide to deploy your Spring Boot backend to Render using GitHub Actions.

## 📋 Prerequisites

- GitHub account with repository
- [Render.com](https://render.com) account (free tier available)
- Docker image pushed to Docker Hub or GitHub Container Registry

## 🚀 Step-by-Step Setup

### Step 1: Create Render Account

1. Go to [render.com](https://render.com)
2. Click "Get Started" → Sign up with GitHub or email
3. Verify email

### Step 2: Create Web Service on Render

1. From Render dashboard, click **"New +"** → **"Web Service"**
2. Choose deployment source:
   - **Option A: GitHub Repository** (Recommended)
     - Click "Connect account" and authorize GitHub
     - Select your repository
     - Choose branch: `main`
   
   - **Option B: Docker Image**
     - Select "Docker"
     - Enter image URL: `ghcr.io/your-username/inventory-dashboard/backend:main`
     - (Use your GHCR or Docker Hub image)

3. Configure service:
   ```
   Name: inventory-movement-backend
   Region: Choose closest to you
   Branch: main
   Build Command: (leave empty if using Docker image)
   Start Command: (leave empty if using Docker image)
   ```

4. Environment variables (click "Advanced"):
   ```
   SPRING_PROFILES_ACTIVE=production
   JAVA_OPTS=-Xmx512m
   ```

5. Click **"Create Web Service"**

### Step 3: Get Deploy Hook URL

1. Go to your Render service settings
2. Navigate to **"Settings"** → **"Deploy Hook"**
3. Copy the Deploy Hook URL
4. It should look like: `https://api.render.com/deploy/srv-xxxxxxxxxxxxx?key=xxxxxxxx`

### Step 4: Add GitHub Secret

1. Go to your GitHub repository
2. Settings → **Secrets and variables** → **Actions**
3. Click **"New repository secret"**
4. Add secret:
   ```
   Name: RENDER_DEPLOY_HOOK
   Value: https://api.render.com/deploy/srv-xxxxxxxxxxxxx?key=xxxxxxxx
   ```
5. Click **"Add secret"**

### Step 5: Configure GitHub Actions

The workflow is already set up to trigger Render deployment. It will:

1. **Build and push Docker image** to GHCR
2. **Run tests**
3. **Trigger Render deploy** on successful completion

### Step 6: Update Render Service (if using Docker Image)

1. In Render dashboard, go to your service
2. Click **"Settings"** → **"Docker Command"** (if needed)
3. Set start command:
   ```bash
   java -jar /app/app.jar
   ```

## 🔄 Deployment Flow

```
Push to main branch
    ↓
GitHub Actions triggered
    ↓
Build Docker image
    ↓
Push to GHCR/Docker Hub
    ↓
Run tests
    ↓
Trigger Render Deploy Hook
    ↓
Render pulls latest image
    ↓
Container starts on Render
    ↓
✅ Deployed!
```

## 📊 Environment Mapping

| Environment | Branch | Action |
|-------------|--------|--------|
| Production | `main` | Auto-deploy |
| Staging | `develop` | Manual deploy |

To add staging (optional):
1. Create separate Render service
2. Add staging workflow (see below)

## 🔧 Optional: Add Staging Environment

Create `.github/workflows/deploy-backend-staging.yml`:

```yaml
name: Build and Deploy Backend to Render Staging

on:
  push:
    branches:
      - develop
    paths:
      - 'backend/**'

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven
      
      - name: Build Backend
        working-directory: ./backend
        run: mvn clean package -DskipTests
      
      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3
      
      - name: Log in to GHCR
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}
      
      - name: Build and push Docker image
        uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: ghcr.io/${{ github.repository }}/backend:develop
      
      - name: Deploy to Render Staging
        run: curl --request POST --url ${{ secrets.RENDER_DEPLOY_HOOK_STAGING }}
        env:
          RENDER_DEPLOY_HOOK_STAGING: ${{ secrets.RENDER_DEPLOY_HOOK_STAGING }}
```

## 🌍 Getting Your Service URL

After first deployment:

1. Go to Render dashboard
2. Click your service
3. Find **"Service URL"** at the top
   - Example: `https://inventory-movement-backend.onrender.com`

4. Update frontend API URL:
   - In `frontend/.env` or `frontend/vite.config.ts`:
   ```typescript
   VITE_API_URL=https://inventory-movement-backend.onrender.com
   ```

## 📝 Important Notes

### Free Tier Limits
- **500 hours/month** of runtime (enough for 24/7)
- **0.5 CPU + 512MB RAM** (basic tier)
- **Auto-sleep after 15 minutes** of inactivity (spin-up time ~30s)

### Keep Service Awake (Optional)
If you want to avoid spin-up delays:
1. Upgrade to **Starter** tier ($7/month)
2. Or use a free uptime monitor to ping your service

### Health Checks
Render automatically checks your service health:
- Expects HTTP 200-299 response on `/` or configured path
- If unhealthy, it will restart

Configure health check in Render Settings:
```
Health Check Path: /actuator/health (Spring Boot)
```

## 🔍 Monitoring Deployments

### GitHub Actions
1. Push code to `main`
2. Go to **Actions** tab
3. Watch deployment progress

### Render Dashboard
1. Go to your service
2. View **Events** for deployment status
3. Check **Logs** for runtime errors

### Logs
```bash
# View Render logs
# In Render dashboard: Logs tab
# Or use Render CLI:
render logs --service inventory-movement-backend
```

## 🐛 Troubleshooting

### Service won't start
- Check logs in Render dashboard
- Verify Java version: `java -version`
- Check Spring Boot health endpoint: `/actuator/health`

### Deploy hook not triggering
- Verify secret is correct in GitHub
- Check GitHub Actions logs for errors
- Ensure webhook URL is accessible

### Image pull fails
- Verify image exists on GHCR/Docker Hub
- Check image tag matches (e.g., `main`, `develop`)
- Ensure image is public (for free tier)

### Application errors in Render
- Check environment variables are set
- Verify database connections (if using DB)
- Review Spring Boot logs in Render dashboard

## ✅ Verification Checklist

- [ ] Render account created
- [ ] Web Service created on Render
- [ ] Deploy Hook URL copied
- [ ] `RENDER_DEPLOY_HOOK` secret added to GitHub
- [ ] Docker image builds successfully
- [ ] Tests pass
- [ ] First deployment successful
- [ ] Service URL accessible
- [ ] Frontend configured to use backend URL

## 📚 Useful Links

- [Render Documentation](https://render.com/docs)
- [Render Pricing](https://render.com/pricing)
- [Spring Boot on Render](https://render.com/docs/deploy-spring-boot)
- [Render CLI](https://render.com/docs/cli)

## 🚀 Next Steps

1. Deploy to Render ✅
2. Update frontend API URL
3. Test end-to-end flow
4. Monitor deployments
5. Scale as needed

---

**Questions?** Check Render docs or GitHub Issues for your repo.
