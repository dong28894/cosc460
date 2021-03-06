package simpledb;

import simpledb.Predicate.Op;

/**
 * A class to represent a fixed-width histogram over a single integer-based field.
 */
public class IntHistogram {
	int min;
	int max;
	int width;
	int lastWidth;
	int ntups;
	int buckets;
	int[] bucketMap;
    /**
     * Create a new IntHistogram.
     * <p/>
     * This IntHistogram should maintain a histogram of integer values that it receives.
     * It should split the histogram into "buckets" buckets.
     * <p/>
     * The values that are being histogrammed will be provided one-at-a-time through the "addValue()" function.
     * <p/>
     * Your implementation should use space and have execution time that are both
     * constant with respect to the number of values being histogrammed.  For example, you shouldn't
     * simply store every value that you see in a sorted list.
     *
     * @param buckets The number of buckets to split the input value into.
     * @param min     The minimum integer value that will ever be passed to this class for histogramming
     * @param max     The maximum integer value that will ever be passed to this class for histogramming
     */
    public IntHistogram(int buckets, int min, int max) {
        // some code goes here
    	this.min = min;
    	this.max = max;
    	if (buckets > (max - min + 1)){
    		this.buckets = max - min + 1;
    	}else{
    		this.buckets = buckets;
    	}
    	ntups = 0;
    	width = (int) Math.floor((max-min+1)/this.buckets);
    	lastWidth = max-min + 1 -(this.buckets-1)*width;
    	bucketMap = new int[this.buckets];
    }

    /**
     * Add a value to the set of values that you are keeping a histogram of.
     *
     * @param v Value to add to the histogram
     */
    public void addValue(int v) {
        // some code goes here
    	if (v > max || v < min){
    		throw new RuntimeException();
    	}else{
    		int bucketIndex = (int) Math.floor(((double)(v-min))/width);
    		if (bucketIndex < (buckets-1)){
    			bucketMap[bucketIndex]++;    			
    		}else{
    			bucketMap[buckets-1]++;
    		}
    		ntups++;
    	}
    }

    /**
     * Estimate the selectivity of a particular predicate and operand on this table.
     * <p/>
     * For example, if "op" is "GREATER_THAN" and "v" is 5,
     * return your estimate of the fraction of elements that are greater than 5.
     *
     * @param op Operator
     * @param v  Value
     * @return Predicted selectivity of this particular operator and value
     */
    public double estimateSelectivity(Predicate.Op op, int v) {
    	int bucketIndex;
    	int leftOfLast = min+width*(buckets-1);
    	if (v >= leftOfLast && v <= max){
    		bucketIndex = buckets - 1;
    	}else if (v < leftOfLast && v >= min){    		
    		bucketIndex = (int) Math.floor(((double)(v-min))/width);
    	}else{
    		bucketIndex = -1;
    	}
    	
        if (op == Op.EQUALS || op == Op.LIKE){
        	if (v > max || v < min){
        		return 0;
        	}else if (bucketIndex < (buckets-1)){
        		return (((double)bucketMap[bucketIndex])/width)/ntups;
        	}else{
        		return (((double)bucketMap[bucketIndex])/lastWidth)/ntups;
        	}
        }else if (op == Op.GREATER_THAN || op == Op.GREATER_THAN_OR_EQ){
        	if (v > max){
        		return 0.0;
        	}else if (v < min){
        		return 1.0;
        	}else{
        		double total;        		
        		if (bucketIndex < (buckets-1)){    
        			int bucketRight = min + width*(bucketIndex+1);
            		if (op == Op.GREATER_THAN_OR_EQ){
            			total = (bucketRight - v)*(((double)bucketMap[bucketIndex])/width);
            		}else{
            			total = (bucketRight - v - 1)*(((double)bucketMap[bucketIndex])/width);
            		}
            	}else{
            		int bucketRight = max;
            		if (op == Op.GREATER_THAN_OR_EQ){
            			total = (bucketRight - v + 1)*(((double)bucketMap[bucketIndex])/lastWidth);
            		}else{
            			total = (bucketRight - v)*(((double)bucketMap[bucketIndex])/lastWidth);
            		}
            	}
        		for (int i = bucketIndex+1; i < buckets; i++){
            		total += bucketMap[i];
            	}
            	return total/ntups;
        	}          	
        }else if (op == Op.LESS_THAN || op == Op.LESS_THAN_OR_EQ){
        	if (v < min){
        		return 0.0;
        	}else if (v > max){
        		return 1.0;
        	}else{
        		int bucketLeft = min + width*bucketIndex;
        		double total;
        		if (bucketIndex < (buckets-1)){
        			if (op == Op.LESS_THAN){
        				total = (v-bucketLeft)*(((double)bucketMap[bucketIndex])/width); 
        			}else{
        				total = (v-bucketLeft+1)*(((double)bucketMap[bucketIndex])/width);
        			}
        		}else{
        			if (op == Op.LESS_THAN){
        				total = (v-bucketLeft)*(((double)bucketMap[bucketIndex])/lastWidth); 
        			}else{
        				total = (v-bucketLeft+1)*(((double)bucketMap[bucketIndex])/lastWidth);
        			}
        		}
        		for (int i = bucketIndex - 1; i >= 0; i--){
        			total += bucketMap[i];
        		}
        		return total/ntups;
        	}
        }else{
        	if (v > max || v < min){
        		return 1.0;
        	}else{
        		if (bucketIndex < (buckets-1)){
        			return (((double)ntups)-bucketMap[bucketIndex]/width)/ntups;
        		}else{        			
        			return (((double)ntups)-bucketMap[bucketIndex]/lastWidth)/ntups;
        		}
        	}
        }
    }

    /**
     * @return A string describing this histogram, for debugging purposes
     */
    public String toString() {
        // some code goes here
        String str = "";
        for (int i = 0; i < buckets; i++){
        	if (i == buckets-1){
        		str += (min + width*i) + " -> " + max + ": " + bucketMap[i] + "\n";
        	}else{
        		str += (min + width*i) + " -> " + (min + width*(i+1)) + ": " + bucketMap[i] + "\n";
        	}
        }
        return str;
    }
}
