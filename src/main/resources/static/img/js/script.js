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

 function initMap() {
            var map = new google.maps.Map(document.getElementById('map'), {
                zoom: 15,
                center: {lat: -34.397, lng: 150.644}
            });

            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(function(position) {
                    var pos = {
                        lat: position.coords.latitude,
                        lng: position.coords.longitude
                    };

                    currentMarker = new google.maps.Marker({
                        position: pos,
                        map: map
                    });

                    map.setCenter(pos);
                }, function() {
                    handleLocationError(true, map.getCenter());
                });
            } else {
                // Browser doesn't support Geolocation
                handleLocationError(false, map.getCenter());
            }

            // Add click event listener to the map
            map.addListener('click', function(event) {
                placeMarker(event.latLng, map);
            });
        }

        function placeMarker(location, map) {
            if (currentMarker) {
                currentMarker.setMap(null); // Remove the previous marker
            }
            currentMarker = new google.maps.Marker({
                position: location,
                map: map
            });
            map.panTo(location);
        }

        function handleLocationError(browserHasGeolocation, pos) {
            var infoWindow = new google.maps.InfoWindow({
                position: pos,
                content: browserHasGeolocation ?
                    'Error: The Geolocation service failed.' :
                    'Error: Your browser doesn\'t support geolocation.'
            });
            infoWindow.open(map);
        }