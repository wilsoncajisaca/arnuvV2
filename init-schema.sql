--INGRESO DEL CORREO QUE VA ENVIAR LOS CORREOS.
CREATE TABLE public.parametros (
	id serial4 NOT NULL,
	archivos bytea NULL,
	codigo varchar(255) NULL,
	descripcion varchar(255) NULL,
	estado bool NULL,
	valor_number float8 NULL,
	valor_text varchar(255) NULL,
	CONSTRAINT parametros_pkey PRIMARY KEY (id),
	CONSTRAINT uk_ilwfiu5mo64homkl4ptmi81ud UNIQUE (codigo)
);

INSERT INTO public.parametros
(id, archivos, codigo, descripcion, estado, valor_number, valor_text)
VALUES(1, NULL, 'MAILSENDER', NULL, true, NULL, 'example@gmail.com');

INSERT INTO public.parametros
(id, archivos, codigo, descripcion, estado, valor_number, valor_text)
VALUES(2, NULL, 'MAILPASSWORD', NULL, true, NULL, 'xxxxxxxx');