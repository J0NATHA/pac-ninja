module info
{
	exports com.bngames.graficos;
	exports com.bngames.entities;
	exports com.bngames.main;
	exports com.bngames.world;

	requires transitive java.desktop;
	requires com.google.gson;
	
	opens com.bngames.main;
}