# Fix IDE Compilation Error

## Problem
The IDE shows: `java.lang.ExceptionInInitializerError` with `javac 25 was used to compile java sources`

This happens because the IDE is using the wrong Java version or has a corrupted cache.

## Solution

### For IntelliJ IDEA:

1. **Set Project SDK to Java 17:**
   - Go to `File` → `Project Structure` (⌘;)
   - Under `Project Settings` → `Project`:
     - Set `SDK` to `17` (or `17.0.16`)
     - Set `Language level` to `17 - Sealed types, always-strict floating-point semantics`

2. **Set Module Language Level:**
   - In the same dialog, go to `Modules` → `milk-management-backend`
   - Set `Language level` to `17 - Sealed types, always-strict floating-point semantics`

3. **Invalidate Caches:**
   - Go to `File` → `Invalidate Caches...`
   - Check all options
   - Click `Invalidate and Restart`

4. **Verify Maven Settings:**
   - Go to `File` → `Settings` (⌘,)
   - Navigate to `Build, Execution, Deployment` → `Build Tools` → `Maven`
   - Ensure `Maven home directory` is set correctly
   - Under `Runner`, set `JRE` to `Use Project JDK (17)`

5. **Reimport Maven Project:**
   - Right-click on `pom.xml` in the project tree
   - Select `Maven` → `Reload Project`

### For VS Code:

1. **Install Java Extension Pack** (if not already installed)

2. **Configure Java Version:**
   - Open Command Palette (⌘⇧P)
   - Type: `Java: Configure Java Runtime`
   - Select Java 17

3. **Set in settings.json:**
   ```json
   {
     "java.configuration.runtimes": [
       {
         "name": "JavaSE-17",
         "path": "/opt/homebrew/Cellar/openjdk@17/17.0.16/libexec/openjdk.jdk/Contents/Home"
       }
     ],
     "java.jdt.ls.java.home": "/opt/homebrew/Cellar/openjdk@17/17.0.16/libexec/openjdk.jdk/Contents/Home"
   }
   ```

4. **Reload Window:**
   - Command Palette → `Developer: Reload Window`

### Alternative: Use Maven from Terminal

If the IDE continues to have issues, you can compile and run from terminal:

```bash
# Compile
cd backend
mvn clean compile

# Run
mvn spring-boot:run
```

The IDE should now compile successfully with Java 17!
