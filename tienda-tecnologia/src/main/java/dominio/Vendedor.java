package dominio;

import dominio.repositorio.RepositorioProducto;
import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;

public class Vendedor {

    public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";

    private RepositorioProducto repositorioProducto;
    private RepositorioGarantiaExtendida repositorioGarantia;

    public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia) {
        this.repositorioProducto = repositorioProducto;
        this.repositorioGarantia = repositorioGarantia;

    }

    public void generarGarantia(String codigo) {
    	
    	if(codigoContarTresVocales(codigo) == true) {
    		throw new GarantiaExtendidaException("Este producto no cuenta con garantía extendida");
    	}
    	else {
    		GarantiaExtendida garantia = this.repositorioGarantia.obtener(codigo);
    		if(garantia == null) {
    			Producto producto = this.repositorioProducto.obtenerPorCodigo(codigo);
        		GarantiaExtendida newGarantia = new GarantiaExtendida(producto);
        		this.repositorioGarantia.agregar(newGarantia);    		
        	}
    	}
    	

    }

    public boolean tieneGarantia(String codigo) {
        return false;
    }
    
    public boolean codigoContarTresVocales(String codigo) {
    	int contadorVocales = 0;
    	String vocales = "aAeEiIoOuU";
    	for (int i = 0; i < codigo.length(); i++) {
    		for (int j = 0; j < vocales.length(); j++) {
    			if(codigo.charAt(i)==vocales.charAt(j)) {
    				contadorVocales++;
    				break;
    			}    				
			}	
    		if(contadorVocales >= 3) {
    			return true;
    		}
		}
    	return false;
    }

}
