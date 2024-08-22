	//valida que no ingresen  puntos ni comas en el formulario para nombres de las mascotas
	function validarNombre(input) {
        // Expresión regular para caracteres permitidos: letras, números y espacios
        const regex = /^[A-Za-z0-9\s]*$/;
        // Reemplaza los caracteres no permitidos con una cadena vacía
        input.value = input.value.replace(/[^A-Za-z0-9\s]/g, '');
    }
function validarEdad(input) {
    // Reemplaza los caracteres que no sean dígitos con una cadena vacía
    input.value = input.value.replace(/[^0-9]/g, '');
}
/// valida que no se ingrese ,./*52# en nombre y aellido de la persona
function validaNombreApellido(input) {
    // Expresión regular para permitir solo letras y espacios
    const regex = /^[A-Za-z\s]*$/;
    // Reemplaza los caracteres no permitidos con una cadena vacía
    input.value = input.value.replace(/[^A-Za-z\s]/g, '');
}
// valida numero de celular

function validaNumeroCelular(input) {
    // Reemplaza los caracteres que no sean dígitos con una cadena vacía
    input.value = input.value.replace(/[^0-9]/g, '');

    // Verifica que el número tenga exactamente 10 dígitos
    if (input.value.length !== 10) {
        mostrarMensajeError('El número debe tener exactamente 10 dígitos.');
    } else {
        ocultarMensajeError();
    }
}

function mostrarMensajeError(mensaje) {
    let mensajeError = document.getElementById('mensaje-error');
    if (!mensajeError) {
        mensajeError = document.createElement('div');
        mensajeError.id = 'mensaje-error';
        mensajeError.style.color = 'red';
        document.querySelector('form').appendChild(mensajeError);
    }
    mensajeError.textContent = mensaje;
}

function ocultarMensajeError() {
    const mensajeError = document.getElementById('mensaje-error');
    if (mensajeError) {
        mensajeError.textContent = '';
    }
}




//validad el correo

function validarEmail(input) {
    // Expresión regular para validar el formato del email
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    // Si el valor no coincide con la expresión regular, establece el valor vacío
    if (!regex.test(input.value)) {
        input.value = '';
    }
}

// valida cedula

function validaIdentificacion(input) {
    // Reemplaza los caracteres no numéricos con una cadena vacía
    //input.value = input.value.replace(/[^0-9]/g, '');

    const identificacion = input.value;

    if (identificacion.length === 10) {
        // Validar cédula
        if (!validarCedula(identificacion)) {
            mostrarMensajeError('Cédula inválida.');
        } else {
            ocultarMensajeError();
        }
    } else if (identificacion.length >= 9 && identificacion.length <= 12) {
        // Validar pasaporte (simplificado)
        ocultarMensajeError();
    } else {
        mostrarMensajeError('La identificación Pasaporte debe tener entre 9 y 12 dígitos.');
    }
}

function validarCedula(cedula) {
    const provincias = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
    if (cedula.length !== 10 || !/^\d+$/.test(cedula)) return false;

    const provincia = parseInt(cedula.substring(0, 2), 10);
    if (!provincias.includes(provincia)) return false;

    const digitoVerificador = parseInt(cedula.charAt(9), 10);
    const sumatoria = cedula
        .substring(0, 9)
        .split('')
        .map((digit, index) => {
            const n = parseInt(digit, 10);
            return index % 2 === 0 ? n * 2 : n;
        })
        .map(n => (n > 9 ? n - 9 : n))
        .reduce((a, b) => a + b, 0);

    const modulo = sumatoria % 10;
    const verificador = modulo === 0 ? 0 : 10 - modulo;

    return verificador === digitoVerificador;
}

function mostrarMensajeError(mensaje) {
    let mensajeError = document.getElementById('mensaje-error');
    if (!mensajeError) {
        mensajeError = document.createElement('div');
        mensajeError.id = 'mensaje-error';
        mensajeError.style.color = 'red';
        document.querySelector('form').appendChild(mensajeError);
    }
    mensajeError.textContent = mensaje;
}

function ocultarMensajeError() {
    const mensajeError = document.getElementById('mensaje-error');
    if (mensajeError) {
        mensajeError.textContent = '';
    }
}