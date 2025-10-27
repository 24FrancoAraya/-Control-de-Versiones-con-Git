import sqlite3
import hashlib # Usaremos hashlib.sha256

def hash_password(password):
    """
    Genera un hash SHA256 para la contraseña. 
    (Mejora de seguridad respecto a MD5 y texto plano)
    """
    # Preferible a MD5. Utilizar bcrypt o argon2 sería aún mejor.
    return hashlib.sha256(password.encode()).hexdigest()

def crear_tabla():
    """Crea la tabla 'usuarios' si no existe, asegurando el cierre de la conexión."""
    try:
        # Uso 'with' para garantizar que conn.close() se llame automáticamente.
        with sqlite3.connect('usuarios.db') as conn:
            cursor = conn.cursor()
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS usuarios (
                    rut TEXT PRIMARY KEY,
                    nombre TEXT NOT NULL,
                    apellido TEXT NOT NULL,
                    patente TEXT NOT NULL,
                    contraseña TEXT NOT NULL 
                )
            """)
            conn.commit()
    except sqlite3.Error as e:
        print(f"Error al crear tabla de usuarios: {e}")

def registrar_usuario(rut, nombre, apellido, patente, contraseña):
    """Registra un nuevo usuario con la contraseña hasheada (SHA256)."""
    
    # [CORRECCIÓN 2 y 3] Uso de SHA256 en lugar de MD5.
    contraseña_hashed = hash_password(contraseña) 
    
    try:
        with sqlite3.connect('usuarios.db') as conn:
            cursor = conn.cursor()
            # [CORRECCIÓN 1] Uso de parámetros (?) para prevenir Inyección SQL.
            cursor.execute("""
                INSERT INTO usuarios (rut, nombre, apellido, patente, contraseña)
                VALUES (?, ?, ?, ?, ?)""", 
                (rut, nombre, apellido, patente, contraseña_hashed))
            conn.commit()
        return True
    except sqlite3.IntegrityError:
        return False
    except sqlite3.Error as e:
        print(f"Error al registrar usuario: {e}")
        return False

def login_usuario(rut, contraseña):
    """
    Verifica el login, comparando el hash de la contraseña ingresada
    con el hash almacenado en la base de datos.
    """
    
    # Hashear la contraseña ingresada por el usuario para compararla.
    contraseña_hashed = hash_password(contraseña)
    
    try:
        with sqlite3.connect('usuarios.db') as conn:
            cursor = conn.cursor()
            
            # [CORRECCIÓN 1] Consulta segura usando parámetros (?)
            # Solo se pide la contraseña almacenada para comparar el hash.
            cursor.execute("SELECT contraseña FROM usuarios WHERE rut=?", (rut,))
            usuario_db = cursor.fetchone()
            
            if usuario_db:
                contraseña_almacenada_hash = usuario_db[0]
                # Comparamos el hash generado de la contraseña ingresada con el hash de la DB.
                return contraseña_hashed == contraseña_almacenada_hash
                
            return False # Usuario no encontrado
            
    except sqlite3.Error as e:
        print(f"Error en login: {e}")
        return False

# Inicializar la tabla al inicio
crear_tabla()