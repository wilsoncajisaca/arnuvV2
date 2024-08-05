function data() {
	function getThemeFromLocalStorage() {
		// if user already changed the theme, use it
		if (window.localStorage.getItem('dark')) {
			return JSON.parse(window.localStorage.getItem('dark'))
		}

		// else return their preferences
		return (
			!!window.matchMedia &&
			window.matchMedia('(prefers-color-scheme: dark)').matches
		)
	}

	function setThemeToLocalStorage(value) {
		window.localStorage.setItem('dark', value)
	}

	return {
		dark: getThemeFromLocalStorage(),
		toggleTheme() {
			this.dark = !this.dark
			setThemeToLocalStorage(this.dark)
		},
		isSideMenuOpen: false,
		toggleSideMenu() {
			this.isSideMenuOpen = !this.isSideMenuOpen
		},
		closeSideMenu() {
			this.isSideMenuOpen = false
		},
		isNotificationsMenuOpen: false,
		toggleNotificationsMenu() {
			this.isNotificationsMenuOpen = !this.isNotificationsMenuOpen
		},
		closeNotificationsMenu() {
			this.isNotificationsMenuOpen = false
		},
		isProfileMenuOpen: false,
		toggleProfileMenu() {
			this.isProfileMenuOpen = !this.isProfileMenuOpen
		},
		closeProfileMenu() {
			this.isProfileMenuOpen = false
		},
		isPagesMenuOpen: false,
		togglePagesMenu() {
			this.isPagesMenuOpen = !this.isPagesMenuOpen
		},
		// Modal
		isModalOpen: false,
		trapCleanup: null,
		openModal() {
			this.isModalOpen = true
			this.trapCleanup = focusTrap(document.querySelector('#modal'))
		},
		closeModal() {
			this.isModalOpen = false
			this.trapCleanup()
		},
	}
}

//---------------------------------------------------------------------------------------------------
function initMap1() {
	var map = new google.maps.Map(document
		.getElementById('map'), {
		zoom: 15
	});

	// Intentar geolocalizar al usuario
	if (navigator.geolocation) {
		navigator.geolocation
			.getCurrentPosition(
				function(position) {
					var pos = {
						lat: position.coords.latitude,
						lng: position.coords.longitude
					};

					// Centrar el mapa en la ubicación del usuario
					map.setCenter(pos);

					// Colocar un marcador en la ubicación del usuario
					var marker = new google.maps.Marker(
						{
							position: pos,
							map: map,
							title: 'Tu ubicación'
						});

					// Llenar los campos de latitud y longitud
					document
						.getElementById('latitud').value = pos.lat;
					document
						.getElementById('longitud').value = pos.lng;

					var latlng = { lat: pos.lat, lng: pos.lng };

					// Geocoder
					var geocoder = new google.maps.Geocoder();
					geocodeLatLng(geocoder, latlng);

				}, function() {
					handleLocationError(true,
						map.getCenter());
				});
	} else {
		// El navegador no soporta geolocalización
		handleLocationError(false, map.getCenter());
	}

	// Añadir marcadores para cada ubicación desde el servidor
	var locations = /*[[${locations}]]*/[];
	locations.forEach(function(location) {
		var marker = new google.maps.Marker({
			position: {
				lat: location.latitude,
				lng: location.longitude
			},
			map: map,
			title: location.description
		});
	});

	// Capturar el clic en el mapa
	map
		.addListener(
			'click',
			function(event) {
				document
					.getElementById('latitud').value = event.latLng.lat();
				document
					.getElementById('longitud').value = event.latLng
						.lng();

				var latlng = { lat: event.latLng.lat(), lng: event.latLng.lng() };



				// Geocoder
				var geocoder = new google.maps.Geocoder();
				geocodeLatLng(geocoder, latlng);

				// Mover el marcador a la nueva ubicación
				console.log("aqui javi");
				var position = {
					lat: event.latLng.lat(),
					lng: event.latLng.lng()

				};
				new google.maps.Marker({
					position: position,
					map: map,
					title: categoria.nombre
				});
				/*marker
					.setPosition(event.latLng);
				map.setCenter(event.latLng);
				*/
			});
}

//---------------------------------------------------------------------------------------------------



function initMap() {
	var map = new google.maps.Map(document.getElementById('map'), {
		zoom: 15,
		center: { lat: -34.397, lng: 150.644 }
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
			updateLatLongFields(pos.lat, pos.lng);

			var latlng = { lat: pos.lat, lng: pos.lng };
			// Geocoder
			var geocoder = new google.maps.Geocoder();
			geocodeLatLng(geocoder, latlng);

		}, function() {
			handleLocationError(true, map.getCenter());
		});
	} else {
		// Browser doesn't support Geolocation
		handleLocationError(false, map.getCenter());
	}
	debugger;
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
	updateLatLongFields(location.lat(), location.lng());
	var latlng = { lat: location.lat(), lng: location.lng() };
	// Geocoder
	var geocoder = new google.maps.Geocoder();
	geocodeLatLng(geocoder, latlng);
}

function updateLatLongFields(lat, lng) {
	document.getElementById('latitud').value = lat;
	document.getElementById('longitud').value = lng;
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
////////////----------------------------------------------------------------------------

function handleLocationError(browserHasGeolocation, pos) {
	var infoWindow = new google.maps.InfoWindow({
		map: map
	});
	infoWindow.setPosition(pos);
	infoWindow
		.setContent(browserHasGeolocation ? 'Error: El servicio de geolocalización falló.'
			: 'Error: Tu navegador no soporta geolocalización.');
}

function showPoint() {
	var lat = parseFloat(document
		.getElementById('latitud').value);
	var lng = parseFloat(document
		.getElementById('longitud').value);
	var pos = {
		lat: lat,
		lng: lng
	};

	// Mover el marcador a la ubicación ingresada
	if (marker) {
		marker.setPosition(pos);
	} else {
		marker = new google.maps.Marker({
			position: pos,
			map: map
		});
	}
	map.setCenter(pos);
}


function geocodeLatLng(geocoder, latlng) {
	geocoder.geocode({ 'location': latlng }, function(results, status) {
		if (status === 'OK') {
			if (results[0]) {
				console.log(results[0]);

				// Dirección Formateada
				document.getElementById('Sector').value = results[0].formatted_address; //results[0].address_components[0].long_name;



			} else {
				console.log('No se encontraron resultados');
			}
		} else {
			console.log('Geocoder falló debido a: ' + status);
		}
	});
}


 async function initMap12() {
							
							 const apiURL = 'http://127.0.0.1:8087/paseos/listar';

							 try {
								 // Hacer una petición GET al API
								 const response = await fetch(apiURL);
								 if (!response.ok) {
									 throw new Error('Error en la respuesta de la red');
								 }
								 const textoJSON = await response.json();
								 
								 var map = new google.maps.Map(document.getElementById('map'), {
								 zoom: 14,
								 center: { lat: textoJSON.latitud, lng: textoJSON.longitud } // Centrar el mapa en una ubicación predeterminada
							 });
							

								

								 const listaUbicacionesDetalle =textoJSON.ubicacionDetalleResponse;

								 listaUbicacionesDetalle.forEach(point => {
									console.log("aqui javi -----------", point.latitud);
									 const marker = new google.maps.Marker({
										 position: { lat: point.latitud, lng: point.longitud },
										 map: map,
										 title: point.idpersona,
										 icon: 'http://maps.google.com/mapfiles/ms/icons/red-dot.png'
									 });

									 // Maneja el clic en el marcador
									 marker.addListener("click", () => {
										 // Actualiza el combo box
										 marker.setIcon('http://maps.google.com/mapfiles/ms/icons/blue-dot.png'); // Icono seleccionado
										 console.log("aqui javi -----------", point.persona);
										 const selectElement = document.getElementById('personaSelect');
										 selectElement.value = point.idpersona;
										 // Dispara un evento de cambio si es necesario
										 selectElement.dispatchEvent(new Event('change'));
										 infowindow.open(map, marker);
									 });
								 });
							 } catch (error) {
								 console.error('Hubo un problema con la operación de fetch:', error);
							 }
						 }

