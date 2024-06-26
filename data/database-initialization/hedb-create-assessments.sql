--
-- PostgreSQL database dump
--

-- Dumped from database version 14.10 (Homebrew)
-- Dumped by pg_dump version 14.10 (Homebrew)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
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
-- Data for Name: institutions; Type: TABLE DATA; Schema: public; Owner: ars
--

COPY public.institutions (id, active, confirmation_token, creation_date, email, name, nif, token_generation_date) FROM stdin;
1	t	abca428c09862e89	2024-02-06 17:58:21.402146	demo_institution@mail.com	DEMO INSTITUTION	000000000	2024-02-06 17:58:21.402134
2	t	abca428c09862e89	2024-02-06 17:58:21.402146	demo_institution@mail.com	DEMO INSTITUTION-2	000000002	2024-02-06 17:58:21.402134
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: ars
--

COPY public.users (user_type, id, creation_date, name, role, state, institution_id) FROM stdin;
MEMBER	2	2024-02-06 17:58:21.419878	DEMO-MEMBER	MEMBER	ACTIVE	1
VOLUNTEER	3	2024-02-06 17:58:23.732513	DEMO-VOLUNTEER	VOLUNTEER	ACTIVE	\N
\.


--
-- Data for Name: auth_users; Type: TABLE DATA; Schema: public; Owner: ars
--

COPY public.auth_users (auth_type, id, active, email, last_access, password, username, confirmation_token, token_generation_date, user_id) FROM stdin;
DEMO	2	t	demo_member@mail.com	\N	\N	demo-member	\N	\N	2
DEMO	3	t	demo_volunteer@mail.com	\N	\N	demo-volunteer	\N	\N	3
\.


-- Data for Name: activity; Type: TABLE DATA; Schema: public; Owner: ars
--

COPY public.activity (id, application_deadline, creation_date, description, ending_date, name, participants_number_limit, region, starting_date, state, institution_id) FROM stdin;
1	2024-02-06 17:58:21.402146	2024-02-06 17:58:21.402146	Same institution is enrolled and participates	2024-02-08 10:58:21.402146	A1	1	Lisbon	2024-02-07 17:58:21.402146	APPROVED	1
2	2024-02-06 17:58:21.402146	2024-02-06 17:58:21.402146	Same institution is enrolled and participates	2024-02-08 10:58:21.402146	A2	1	Lisbon	2024-02-07 17:58:21.402146	APPROVED	1
3	2024-02-06 17:58:21.402146	2024-02-06 17:58:21.402146	Same institution is enrolled and does not participate	2024-02-08 10:58:21.402146	A3	2	Lisbon	2024-02-07 17:58:21.402146	APPROVED	1
4	2024-02-06 17:58:21.402146	2024-02-06 17:58:21.402146	Same institution is not enrolled	2024-02-08 10:58:21.402146	A4	2	Lisbon	2024-02-07 17:58:21.402146	APPROVED	1
5	2024-02-06 17:58:21.402146	2024-02-06 17:58:21.402146	Same institution before end date	2024-08-08 10:58:21.402146	A5	2	Lisbon	2024-02-07 17:58:21.402146	APPROVED	1
6	2024-02-06 17:58:21.402146	2024-02-06 17:58:21.402146	Other institution is enrolled and participates	2024-02-08 10:58:21.402146	A6	3	Lisbon	2024-02-07 17:58:21.402146	APPROVED	2
\.


--
-- Data for Name: enrollment; Type: TABLE DATA; Schema: public; Owner: ars
--

COPY public.enrollment (id, enrollment_date_time, motivation, activity_id, volunteer_id) FROM stdin;
1	2024-02-06 18:51:37.595713	sql-inserted-motivation	1	3
2	2024-02-06 18:51:37.595713	sql-inserted-motivation	2	3
3	2024-02-06 18:51:37.595713	sql-inserted-motivation	3	3
4	2024-02-06 18:51:37.595713	sql-inserted-motivation	6	3
\.


--
-- Data for Name: participation; Type: TABLE DATA; Schema: public; Owner: ars
--

COPY public.participation (id, acceptance_date, rating, activity_id, volunteer_id) FROM stdin;
1	2024-02-06 18:51:37.595713	5	1	3
2	2024-02-06 18:51:37.595713	5	2	3
3	2024-02-06 18:51:37.595713	5	6	3
\.

--
-- Data for Name: assessment; Type: TABLE DATA; Schema: public; Owner: ars
--

COPY public.assessment (id, review, review_date, institution_id, volunteer_id) FROM stdin;
1	Foi bom	2024-02-06 18:51:37.595713	1	2
2	Isto é um test	2024-02-06 18:51:37.595713	1	2
\.

--
-- PostgreSQL database dump complete
--
