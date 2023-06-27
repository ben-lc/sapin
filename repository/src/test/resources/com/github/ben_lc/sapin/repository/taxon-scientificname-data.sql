INSERT INTO sapin.taxon (id,src_name_id,parent_id,"rank",accepted_name,tree_path) VALUES
	 (2489965,'1',NULL,'KINGDOM','Animalia','2489965'),
	 (1609069,'44',2489965,'PHYLUM','Chordata','2489965.1609069'),
	 (1035228,'212',1609069,'CLASS','Aves','2489965.1609069.1035228'),
	 (529475,'7191147',1035228,'ORDER','Accipitriformes','2489965.1609069.1035228.529475'),
	 (522064,'2877',529475,'FAMILY','Accipitridae','2489965.1609069.1035228.529475.522064'),
	 (1847746,'2480444',522064,'GENUS','Haliaeetus Savigny, 1809','2489965.1609069.1035228.529475.522064.1847746'),
	 (753968,'2480449',1847746,'SPECIES','Haliaeetus albicilla (Linnaeus, 1758)','2489965.1609069.1035228.529475.522064.1847746.753968'),
	 (2544750,'7059939',753968,'SUBSPECIES','Haliaeetus albicilla albicilla','2489965.1609069.1035228.529475.522064.1847746.753968.2544750');

INSERT INTO sapin.taxon_scientific_name (id,taxon_id,src_id,"rank",taxonomic_status,"name",accepted_name_id,original_name_id,canonical_name,authorship,generic_name,specific_epithet,infraspecific_epithet,name_published_in) VALUES
	 (2280150,753968,'4408411','SPECIES','SYNONYM','Falco albicilla Linnaeus, 1758',2576157,NULL,'Falco albicilla','Linnaeus, 1758','Falco','albicilla',NULL,'Linnaeus, C. (1758). Systema Naturae per regna tria naturae, secundum classes, ordines, genera, species, cum characteribus, differentiis, synonymis, locis. <em>Editio decima, reformata [10th revised edition], vol. 1: 824 pp. Laurentius Salvius: Holmiae.</em>'),
	 (2576181,753968,'11358002','SPECIES','SYNONYM','Haliaetus albicilla (L.)',2576157,NULL,'Haliaetus albicilla','(L.)','Haliaetus','albicilla',NULL,NULL),
	 (2576157,753968,'2480449','SPECIES','ACCEPTED','Haliaeetus albicilla (Linnaeus, 1758)',NULL,NULL,'Haliaeetus albicilla','(Linnaeus, 1758)','Haliaeetus','albicilla',NULL,'Syst. Nat. ed. 10 p. 89'),
	 (2576178,1847746,'4850081','GENUS','SYNONYM','Haliaethus Lesson, 1831',2576156,NULL,'Haliaethus','Lesson, 1831','Haliaethus',NULL,NULL,'Traité Orn., (8'),
	 (2576245,1847746,'4850087','GENUS','SYNONYM','Haliaëtos Bonaparte, 1826',2576156,NULL,'Haliaetos','Bonaparte, 1826','Haliaëtos',NULL,NULL,'Ann. Lyceum Hist. Nat. N. York, 2, 1828, 24'),
     (2576156,1847746,'2480444','GENUS','ACCEPTED','Haliaeetus Savigny, 1809',NULL,NULL,'Haliaeetus','Savigny, 1809','Haliaeetus',NULL,NULL,'Descr. Egypte 1 p. 68, 85'),
	 (2576158,2544750,'7059939','SUBSPECIES','ACCEPTED','Haliaeetus albicilla albicilla',NULL,NULL,'Haliaeetus albicilla albicilla','(Linnaeus, 1758)','Haliaeetus','albicilla','albicilla',NULL);
