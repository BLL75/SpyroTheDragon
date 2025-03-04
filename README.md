# 🐉 Proyecto: Actualización de la App de Spyro the Dragon

## 📝 Introducción
En esta actividad, actualizaremos una aplicación inspirada en el universo de *Spyro the Dragon* para mejorar su atractivo y usabilidad. Para ello, desarrollaremos dos elementos clave:

- **Guía de inicio interactiva**: Introducirá al usuario en la app de forma visual y dinámica con animaciones, sonidos y elementos gráficos.
- **Easter Egg**: Se añadirán elementos ocultos que sorprenderán y entretendrán a los usuarios.

## ✨ Características principales
### 🏁 Guía de inicio interactiva
1. **Pantalla 1**: Bienvenida a la guía interactiva con un fondo personalizado inspirado en *Spyro the Dragon* y un botón "Comenzar" con transición animada.
2. **Pantalla 2**: Explicación de la pestaña *Personajes* con un bocadillo animado.
3. **Pantalla 3**: Explicación de la pestaña *Mundos* con un bocadillo animado y opción de interacción automática.
4. **Pantalla 4**: Explicación de la pestaña *Coleccionables* con un bocadillo animado.
5. **Pantalla 5**: Explicación del icono de información con un bocadillo animado.
6. **Pantalla 6**: Resumen final de la guía con botón para comenzar a usar la aplicación.

✔ **Navegación:**
- Botón para avanzar en cada pantalla.
- Botón para omitir la guía.
- Bloqueo de interacción con la app mientras la guía está activa.
- La guía solo se muestra la primera vez que se abre la app (usando *SharedPreferences*).

✔ **Animaciones:**
- Bocadillos animados con efectos de aparición, deslizamiento o ampliación.
- Transiciones entre pantallas con efectos de desvanecimiento, desplazamiento lateral o escala.

✔ **Sonidos:**
- Efectos de sonido temáticos al avanzar, interactuar con bocadillos y completar la guía.

### 🎁 Easter Eggs
1. **Easter Egg con vídeo:**
   - Ubicado en la pestaña de *Coleccionables*.
   - Se activa tras pulsar cuatro veces sobre las *Gemas*.
   - Reproduce un vídeo temático en pantalla completa antes de regresar a *Coleccionables*.

2. **Easter Egg con animación:**
   - Ubicado en la pestaña de *Personajes*.
   - Se activa con una pulsación prolongada sobre *Spyro*.
   - Muestra una animación en *Canvas* simulando una llama de fuego saliendo de la boca de *Spyro*.

## 🔧 Tecnologías utilizadas
- **Kotlin**: Lenguaje principal de desarrollo.
- **Android Studio**: Entorno de desarrollo.
- **SharedPreferences**: Para controlar la visualización única de la guía.
- **Canvas API**: Para la animación del Easter Egg de Spyro.
- **MediaPlayer**: Para la reproducción de sonidos y vídeo.

## 🚀 Instrucciones de uso
### Clonar el repositorio
```sh
 git clone https://github.com/tu_usuario/tu_repositorio.git
 cd tu_repositorio
```
### Instalación de dependencias
Asegúrate de tener *Android Studio* instalado. Luego, abre el proyecto y sincroniza las dependencias.

### Ejecución
1. Ejecuta la app en un emulador o dispositivo físico desde *Android Studio*.
2. La guía de inicio interactiva se mostrará automáticamente al primer inicio.
3. Explora la app y descubre los *Easter Eggs* ocultos.

## 🎯 Conclusiones del desarrollador
El desarrollo de esta aplicación presentó varios desafíos y oportunidades de aprendizaje. Se implementaron elementos interactivos con el objetivo de enriquecer la experiencia del usuario y hacer que la navegación por la app fuera más atractiva e intuitiva. La integración de animaciones permitió que los elementos de la interfaz cobraran vida, facilitando la comprensión de las distintas secciones de la aplicación. Además, los efectos de sonido añadieron una capa de inmersión, reforzando la ambientación inspirada en *Spyro the Dragon*.

Uno de los aspectos más interesantes fue la incorporación de *Easter Eggs*, los cuales no solo ofrecen un elemento de sorpresa y diversión, sino que también incentivan la exploración dentro de la app. El uso de *SharedPreferences* permitió que la guía de inicio interactiva solo se mostrara una vez, garantizando una experiencia fluida sin interrupciones innecesarias.

En general, este proyecto representó una excelente oportunidad para aplicar conocimientos en diseño de interfaces, animaciones, manejo de eventos y almacenamiento de datos. Se logró un producto final atractivo, funcional y alineado con la temática del universo de *Spyro*, mejorando significativamente la usabilidad y el engagement del usuario.


## 📄 Licencia
Este proyecto está bajo la licencia MIT. Puedes ver más detalles en el archivo LICENSE del repositorio.
