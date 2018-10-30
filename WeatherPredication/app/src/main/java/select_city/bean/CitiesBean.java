package select_city.bean;

import java.util.List;

/**
 * Created by kun on 2016/10/26.
 */
public class CitiesBean {

    /**
     * alifName : C
     * addressList : [{"id":37,"name":"潮州"}]
     */

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String alifName;
        /**
         * id : 37
         * name : 潮州
         */

        private List<AddressListBean> addressList;

        public String getAlifName() {
            return alifName;
        }

        public void setAlifName(String alifName) {
            this.alifName = alifName;
        }

        public List<AddressListBean> getAddressList() {
            return addressList;
        }

        public void setAddressList(List<AddressListBean> addressList) {
            this.addressList = addressList;
        }

        public static class AddressListBean {
            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
