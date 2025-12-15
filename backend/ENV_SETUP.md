# Environment Setup

## Database Credentials

The application uses environment variables for sensitive database credentials to avoid hardcoding passwords.

### Setting Environment Variables

**On macOS/Linux:**
```bash
export DB_USERNAME=root
export DB_PASSWORD=your_password_here
```

**On Windows (Command Prompt):**
```cmd
set DB_USERNAME=root
set DB_PASSWORD=your_password_here
```

**On Windows (PowerShell):**
```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_password_here"
```

### Alternative: Create application.properties

If you prefer not to use environment variables, create `src/main/resources/application.properties` from the example:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Then edit `application.properties` and set your database password directly (not recommended for production).

### Default Values

- `DB_USERNAME` defaults to `root` if not set
- `DB_PASSWORD` defaults to empty string if not set

**Important:** Always set `DB_PASSWORD` environment variable in production environments!
