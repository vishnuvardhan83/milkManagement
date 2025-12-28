# 🚀 Quick Commands Reference

## Current Directory
You're in: `/Users/ent-0439/Documents/ownData/milkManagement`

## ✅ Start Backend

### Option 1: Use the startup script (Easiest)
```bash
./START_BACKEND.sh
```

### Option 2: Manual commands
```bash
# Set MySQL password
export DB_PASSWORD=your_mysql_password

# Start backend
cd backend
mvn spring-boot:run
```

### Option 3: One-liner
```bash
export DB_PASSWORD=your_mysql_password && cd backend && mvn spring-boot:run
```

## ✅ Start Frontend

```bash
cd frontend
npm install --legacy-peer-deps  # First time only
ng serve
```

## 🔍 Check Current Directory

```bash
pwd
ls -la
```

## 📁 Project Structure

```
milkManagement/
├── backend/          # Spring Boot backend
├── frontend/         # Angular frontend
├── database/         # SQL scripts
└── START_BACKEND.sh  # Backend startup script
```

## 🐛 Troubleshooting

### "No such file or directory: backend"
- Make sure you're in the project root: `/Users/ent-0439/Documents/ownData/milkManagement`
- Check with: `ls -d backend/`

### MySQL Password Issues
- Run: `./backend/reset-mysql-password.sh`
- Or test: `mysql -u root -p`

### Port Already in Use
- Backend (8080): Change `server.port` in `backend/src/main/resources/application.properties`
- Frontend (4200): Use `ng serve --port 4201`

