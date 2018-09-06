package dominio;

import dominio.repositorio.RepositorioProducto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;

public class Vendedor {

    public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";
    
    public static final String NO_CUENTA_CON_GARANTIA = "Este producto no cuenta con garantía extendida";
    
    private RepositorioProducto repositorioProducto;
    private RepositorioGarantiaExtendida repositorioGarantia;

    public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia) {
        this.repositorioProducto = repositorioProducto;
        this.repositorioGarantia = repositorioGarantia;

    }

    public void generarGarantia(String codigo, String nombreCliente) throws ParseException {    	
    	if(codigoContarTresVocales(codigo) == true) {
    		throw new GarantiaExtendidaException(NO_CUENTA_CON_GARANTIA);
    	}
    	else {
    		GarantiaExtendida garantia = this.repositorioGarantia.obtener(codigo);
    		if(garantia == null) {
    			Producto producto = this.repositorioProducto.obtenerPorCodigo(codigo);
    			if(producto.getPrecio() > 500000) {
    				crearGarantia(producto, 200, nombreCliente, 20);
    			} else {
    				crearGarantia(producto, 100, nombreCliente, 10); 
    			}        		  		
        	} else {
        		throw new GarantiaExtendidaException(EL_PRODUCTO_TIENE_GARANTIA);
        	}
    	}
    }
    
    public void crearGarantia(Producto producto, int dias, String nombreCliente, int porcentaje) throws ParseException {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	Date current_date = sdf.parse("2018-08-16");
    	//Date current_date = new Date();
		Date fechaFinal = obtenerDiaFinalGarantia(current_date, dias);
		double precioGarantia = (producto.getPrecio()*porcentaje)/100;
		GarantiaExtendida newGarantia = new GarantiaExtendida(producto, current_date, fechaFinal, precioGarantia, nombreCliente);
		this.repositorioGarantia.agregar(newGarantia);
    }

    public boolean tieneGarantia(String codigo) {
    	Producto producto = this.repositorioGarantia.obtenerProductoConGarantiaPorCodigo(codigo);
		if(producto == null) {
			return false;
		}
		return true;
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
    
    public Date obtenerDiaFinalGarantia(Date fecha, int cantidadDias) {
		int contDays = 1;
		while(contDays <= cantidadDias) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(fecha);
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			if (dayOfWeek != Calendar.MONDAY) {
				contDays++;				
			}
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			fecha = calendar.getTime();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fecha);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == Calendar.SUNDAY) {
			calendar.add(Calendar.DAY_OF_YEAR, 2);
		}
		return fecha;
	}

}
