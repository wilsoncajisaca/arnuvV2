document.addEventListener("DOMContentLoaded", function() {
    var select = document.getElementById("browserSelect");

    for (var i = 0; i < select.options.length; i++) {
        var option = select.options[i];
        var thumbnail = option.getAttribute("data-thumbnail");
        
        if (thumbnail) {
            var img = document.createElement("img");
            img.src = thumbnail;
            img.alt = "";
            img.classList.add("thumbnail-option");
            option.innerText = ""; // Limpiar el texto actual
            option.appendChild(img);
            option.appendChild(document.createTextNode(option.text));
        }
    }
});