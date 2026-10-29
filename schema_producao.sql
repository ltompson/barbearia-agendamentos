--
-- PostgreSQL database dump
--

\restrict DGrSrLVBrtiaIgBXBkemg8XszGG4Kl4917IAAE1q8P8LKgxRW45lZ3ftJYJRSL7

-- Dumped from database version 17.9
-- Dumped by pg_dump version 17.9

-- Started on 2026-10-09 07:17:17

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 220 (class 1259 OID 16397)
-- Name: admins; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.admins (
    id bigint NOT NULL,
    nome character varying(100) NOT NULL,
    usuario character varying(50) NOT NULL,
    senha character varying(255) NOT NULL,
    criado_em timestamp without time zone DEFAULT now() NOT NULL,
    ativo boolean DEFAULT true NOT NULL,
    role character varying(20) DEFAULT 'FUNCIONARIO'::character varying NOT NULL,
    barbeiro_id bigint
);


--
-- TOC entry 219 (class 1259 OID 16396)
-- Name: admins_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.admins_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4857 (class 0 OID 0)
-- Dependencies: 219
-- Name: admins_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.admins_id_seq OWNED BY public.admins.id;


--
-- TOC entry 226 (class 1259 OID 16425)
-- Name: agendamentos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.agendamentos (
    id bigint NOT NULL,
    cliente_id bigint NOT NULL,
    barbeiro_id bigint NOT NULL,
    servico_id bigint NOT NULL,
    data_hora timestamp without time zone NOT NULL,
    status character varying(20) DEFAULT 'PENDENTE'::character varying NOT NULL,
    criado_em timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 225 (class 1259 OID 16424)
-- Name: agendamentos_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.agendamentos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4858 (class 0 OID 0)
-- Dependencies: 225
-- Name: agendamentos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.agendamentos_id_seq OWNED BY public.agendamentos.id;


--
-- TOC entry 222 (class 1259 OID 16407)
-- Name: barbeiros; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.barbeiros (
    id bigint NOT NULL,
    nome character varying(100) NOT NULL,
    telefone character varying(20) NOT NULL,
    ativo boolean DEFAULT true NOT NULL,
    criado_em timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 221 (class 1259 OID 16406)
-- Name: barbeiros_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.barbeiros_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4859 (class 0 OID 0)
-- Dependencies: 221
-- Name: barbeiros_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.barbeiros_id_seq OWNED BY public.barbeiros.id;


--
-- TOC entry 228 (class 1259 OID 24577)
-- Name: bloqueios; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.bloqueios (
    id bigint NOT NULL,
    data date NOT NULL,
    horario time without time zone,
    barbeiro_id bigint,
    motivo character varying(255)
);


--
-- TOC entry 227 (class 1259 OID 24576)
-- Name: bloqueios_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.bloqueios_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4860 (class 0 OID 0)
-- Dependencies: 227
-- Name: bloqueios_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.bloqueios_id_seq OWNED BY public.bloqueios.id;


--
-- TOC entry 218 (class 1259 OID 16389)
-- Name: clientes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.clientes (
    id bigint NOT NULL,
    nome character varying(100) NOT NULL,
    telefone character varying(20) NOT NULL,
    criado_em timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 217 (class 1259 OID 16388)
-- Name: clientes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.clientes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4861 (class 0 OID 0)
-- Dependencies: 217
-- Name: clientes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.clientes_id_seq OWNED BY public.clientes.id;


--
-- TOC entry 230 (class 1259 OID 40961)
-- Name: dias_disponiveis; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dias_disponiveis (
    id bigint NOT NULL,
    data date NOT NULL,
    barbeiro_id bigint NOT NULL,
    motivo character varying(255),
    horario time without time zone
);


--
-- TOC entry 229 (class 1259 OID 40960)
-- Name: dias_disponiveis_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.dias_disponiveis_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4862 (class 0 OID 0)
-- Dependencies: 229
-- Name: dias_disponiveis_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.dias_disponiveis_id_seq OWNED BY public.dias_disponiveis.id;


--
-- TOC entry 224 (class 1259 OID 16416)
-- Name: servicos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.servicos (
    id bigint NOT NULL,
    nome character varying(100) NOT NULL,
    preco numeric(8,2) NOT NULL,
    duracao_minutos integer NOT NULL,
    ativo boolean DEFAULT true NOT NULL,
    criado_em timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 223 (class 1259 OID 16415)
-- Name: servicos_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.servicos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4863 (class 0 OID 0)
-- Dependencies: 223
-- Name: servicos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.servicos_id_seq OWNED BY public.servicos.id;


--
-- TOC entry 4673 (class 2604 OID 16400)
-- Name: admins id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admins ALTER COLUMN id SET DEFAULT nextval('public.admins_id_seq'::regclass);


--
-- TOC entry 4683 (class 2604 OID 16428)
-- Name: agendamentos id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.agendamentos ALTER COLUMN id SET DEFAULT nextval('public.agendamentos_id_seq'::regclass);


--
-- TOC entry 4677 (class 2604 OID 16410)
-- Name: barbeiros id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.barbeiros ALTER COLUMN id SET DEFAULT nextval('public.barbeiros_id_seq'::regclass);


--
-- TOC entry 4686 (class 2604 OID 24583)
-- Name: bloqueios id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bloqueios ALTER COLUMN id SET DEFAULT nextval('public.bloqueios_id_seq'::regclass);


--
-- TOC entry 4671 (class 2604 OID 16392)
-- Name: clientes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clientes ALTER COLUMN id SET DEFAULT nextval('public.clientes_id_seq'::regclass);


--
-- TOC entry 4687 (class 2604 OID 40964)
-- Name: dias_disponiveis id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dias_disponiveis ALTER COLUMN id SET DEFAULT nextval('public.dias_disponiveis_id_seq'::regclass);


--
-- TOC entry 4680 (class 2604 OID 16419)
-- Name: servicos id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.servicos ALTER COLUMN id SET DEFAULT nextval('public.servicos_id_seq'::regclass);


--
-- TOC entry 4691 (class 2606 OID 16403)
-- Name: admins admins_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admins
    ADD CONSTRAINT admins_pkey PRIMARY KEY (id);


--
-- TOC entry 4693 (class 2606 OID 16405)
-- Name: admins admins_usuario_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admins
    ADD CONSTRAINT admins_usuario_key UNIQUE (usuario);


--
-- TOC entry 4699 (class 2606 OID 16432)
-- Name: agendamentos agendamentos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.agendamentos
    ADD CONSTRAINT agendamentos_pkey PRIMARY KEY (id);


--
-- TOC entry 4695 (class 2606 OID 16414)
-- Name: barbeiros barbeiros_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.barbeiros
    ADD CONSTRAINT barbeiros_pkey PRIMARY KEY (id);


--
-- TOC entry 4701 (class 2606 OID 24585)
-- Name: bloqueios bloqueios_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bloqueios
    ADD CONSTRAINT bloqueios_pkey PRIMARY KEY (id);


--
-- TOC entry 4689 (class 2606 OID 16395)
-- Name: clientes clientes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clientes
    ADD CONSTRAINT clientes_pkey PRIMARY KEY (id);


--
-- TOC entry 4703 (class 2606 OID 40966)
-- Name: dias_disponiveis dias_disponiveis_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dias_disponiveis
    ADD CONSTRAINT dias_disponiveis_pkey PRIMARY KEY (id);


--
-- TOC entry 4697 (class 2606 OID 16423)
-- Name: servicos servicos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.servicos
    ADD CONSTRAINT servicos_pkey PRIMARY KEY (id);


--
-- TOC entry 4704 (class 2606 OID 16438)
-- Name: agendamentos agendamentos_barbeiro_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.agendamentos
    ADD CONSTRAINT agendamentos_barbeiro_id_fkey FOREIGN KEY (barbeiro_id) REFERENCES public.barbeiros(id);


--
-- TOC entry 4705 (class 2606 OID 16433)
-- Name: agendamentos agendamentos_cliente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.agendamentos
    ADD CONSTRAINT agendamentos_cliente_id_fkey FOREIGN KEY (cliente_id) REFERENCES public.clientes(id);


--
-- TOC entry 4706 (class 2606 OID 16443)
-- Name: agendamentos agendamentos_servico_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.agendamentos
    ADD CONSTRAINT agendamentos_servico_id_fkey FOREIGN KEY (servico_id) REFERENCES public.servicos(id);


-- Completed on 2026-10-09 07:17:17

--
-- PostgreSQL database dump complete
--

\unrestrict DGrSrLVBrtiaIgBXBkemg8XszGG4Kl4917IAAE1q8P8LKgxRW45lZ3ftJYJRSL7

