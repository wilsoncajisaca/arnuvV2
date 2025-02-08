--INGRESAR LOS MENUS REALIZADOS HASTA EL MOMENTO.
INSERT INTO public.menu_item
(id, icono, nombre, orden, url, parent_menu_item_id)
VALUES(1, 'fa fa-list', 'Home', '1', '/home', NULL);

INSERT INTO public.menu_item
(id, icono, nombre, orden, url, parent_menu_item_id)
VALUES(2, 'fa fa-paw', 'Tarifarios', '2', '/tarifario/listar', NULL);

INSERT INTO public.menu_item
(id, icono, nombre, orden, url, parent_menu_item_id)
VALUES(3, 'fa fa-rocket', 'Catalogos', '3', '/catalogo/listar', NULL);

INSERT INTO public.menu_item
(id, icono, nombre, orden, url, parent_menu_item_id)
VALUES(4, 'fa fa-user', 'Mascotas', '4', '/mascota/listar', NULL);

INSERT INTO public.menu_item
(id, icono, nombre, orden, url, parent_menu_item_id)
VALUES(5, 'fa fa-rocket', 'Paseos', '5', '/paseo/listar', NULL);

INSERT INTO public.menu_item
(id, icono, nombre, orden, url, parent_menu_item_id)
VALUES(6, 'fas fa-cog mr-2', 'Persona', '6', '/persona/crear', NULL);

INSERT INTO public.menu_item
(id, icono, nombre, orden, url, parent_menu_item_id)
VALUES(7, 'fa fa-check', 'Calificaciones', '7', '/calificacion/listar', NULL);

INSERT INTO public.menu_item
(id, icono, nombre, orden, url, parent_menu_item_id)
VALUES(8, 'fa fa-sliders', 'Parametros', '8', '/parametro/general', NULL);

--INGRESO DE LOS ROLES
INSERT INTO public.rol
(idrol, activo, nombre, idpolitica) VALUES(1, 1, 'ROLE_ADMIN', NULL);

INSERT INTO public.rol
(idrol, activo, nombre, idpolitica) VALUES(2, 1, 'ROLE_CLIENTE', NULL);

INSERT INTO public.rol
(idrol, activo, nombre, idpolitica) VALUES(3, 1, 'ROLE_PASEADOR', NULL);

--ASIGNACION DE UN MENU A UN ROL DE USUARIO (ADMINISTRADOR)
INSERT INTO public.menu_item_role
(menu_item_id, role_id) VALUES(1, 1);

INSERT INTO public.menu_item_role
(menu_item_id, role_id) VALUES(2, 1);

INSERT INTO public.menu_item_role
(menu_item_id, role_id) VALUES(3, 1);

INSERT INTO public.menu_item_role
(menu_item_id, role_id) VALUES(4, 1);

INSERT INTO public.menu_item_role
(menu_item_id, role_id) VALUES(5, 1);

INSERT INTO public.menu_item_role
(menu_item_id, role_id) VALUES(6, 1);

INSERT INTO public.menu_item_role
(menu_item_id, role_id) VALUES(7, 1);

INSERT INTO public.menu_item_role
(menu_item_id, role_id) VALUES(8, 1);

--ASIGNACION DE UN MENU A UN ROL DE USUARIO (CLIENTE)
INSERT INTO public.menu_item_role
(menu_item_id, role_id) VALUES(4, 3);

INSERT INTO public.menu_item_role
(menu_item_id, role_id) VALUES(5, 3);

--ASIGNACION DE UN MENU A UN ROL DE USUARIO (PASEADOR)
INSERT INTO public.menu_item_role
(menu_item_id, role_id) VALUES(1, 2);

INSERT INTO public.menu_item_role
(menu_item_id, role_id) VALUES(5, 2);

INSERT INTO public.menu_item_role
(menu_item_id, role_id) VALUES(7, 2);