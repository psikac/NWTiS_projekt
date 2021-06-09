#MYAIRPORTS

select count(*) from myairports;

#MYAIRPORTSLOG

select count(flightDate) from myairportslog GROUP BY ident;

#AIRPLANES

SELECT count(*) from airplanes;

#METEO

SELECT m.ident, count(m.timestamp) FROM meteo m GROUP BY m.ident;




