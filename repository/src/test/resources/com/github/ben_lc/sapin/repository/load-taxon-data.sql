INSERT INTO
  sapin.taxon (
    id,
    src_name_id,
    parent_id,
    "rank",
    accepted_name,
    tree_path
  )
VALUES
  (
    2489965,
    '1',
    NULL,
    'KINGDOM',
    'Animalia',
    '2489965'
  ),
  (
    1609069,
    '44',
    2489965,
    'PHYLUM',
    'Chordata',
    '2489965.1609069'
  ),
  (
    1035228,
    '212',
    1609069,
    'CLASS',
    'Aves',
    '2489965.1609069.1035228'
  ),
  (
    529475,
    '7191147',
    1035228,
    'ORDER',
    'Accipitriformes',
    '2489965.1609069.1035228.529475'
  ),
  (
    522064,
    '2877',
    529475,
    'FAMILY',
    'Accipitridae',
    '2489965.1609069.1035228.529475.522064'
  ),
  (
    1847746,
    '2480444',
    522064,
    'GENUS',
    'Haliaeetus Savigny, 1809',
    '2489965.1609069.1035228.529475.522064.1847746'
  ),
  (
    753968,
    '2480449',
    1847746,
    'SPECIES',
    'Haliaeetus albicilla (Linnaeus, 1758)',
    '2489965.1609069.1035228.529475.522064.1847746.753968'
  ),
  (
    2544750,
    '7059939',
    753968,
    'SUBSPECIES',
    'Haliaeetus albicilla albicilla',
    '2489965.1609069.1035228.529475.522064.1847746.753968.2544750'
  ),
  (
    2034352,
    '7191407',
    1035228,
    'ORDER',
    'Falconiformes',
    '2489965.1609069.1035228.2034352'
  ),
  (
    2130593,
    '5240',
    2034352,
    'FAMILY',
    'Falconidae',
    '2489965.1609069.1035228.2034352.2130593'
  ),
  (
    1892778,
    '2480996',
    2130593,
    'GENUS',
    'Falco Linnaeus, 1758',
    '2489965.1609069.1035228.2034352.2130593.1892778'
  ),
  (
    162022,
    '2481047',
    1892778,
    'SPECIES',
    'Falco peregrinus Tunstall, 1771',
    '2489965.1609069.1035228.2034352.2130593.1892778.162022'
  );

INSERT INTO
  sapin.taxon_vernacular_name (taxon_id, name, rank, language)
VALUES
  (753968, 'Havørn', 'SPECIES', 'da'),
  (753968, 'White-tailed Eagle', 'SPECIES', 'en'),
  (
    753968,
    'pygargue à queue blanche',
    'SPECIES',
    'fr'
  ),
  (1847746, 'Fish Eagles', 'GENUS', 'en'),
  (1847746, 'havsörnar', 'GENUS', 'sv');
