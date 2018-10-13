package hp.redreader.com.mvp.model.entity.book;


import java.util.List;

/**
 * Created by jingbin on 2016/12/25.
 */

public class BookDetailBean {


    /**
     * rating : {"max":10,"numRaters":13032,"average":"8.8","min":0}
     * subtitle :
     * author : ["[美] 理查德·格里格","菲利普·津巴多"]
     * pubdate : 2003-10
     * tags : [{"count":21991,"name":"心理学","title":"心理学"},{"count":11994,"name":"心理学与生活","title":"心理学与生活"},{"count":4599,"name":"心理","title":"心理"},{"count":2904,"name":"经典","title":"经典"},{"count":2829,"name":"教材","title":"教材"},{"count":2660,"name":"普通心理学","title":"普通心理学"},{"count":1888,"name":"科普","title":"科普"},{"count":1769,"name":"psychology","title":"psychology"}]
     * origin_title :
     * image : https://img3.doubanio.com/view/subject/m/public/s1068520.jpg
     * binding : 平装
     * translator : ["王垒","王甦 等"]
     * catalog : 序言
     第一章 生活中的心理学
     第二章 心理学的研究方法
     第三章 行为的生物学基础
     第四章 感觉
     第五章 知觉
     第六章 心理，意识和其他状态
     第七章 学习与行为分析
     第八章 记忆
     第九章 认知过程
     第十章 智力与智力测验
     第十一章 人的毕生发展
     第十二章 动机
     第十三章 情绪、压力和健康
     第十四章 理解人类人格
     第十五章 心理障碍
     第十六章 心理治疗
     第十七章 社会过程与关系
     第十八章 社会心理学，社会和文化
     * pages : 621
     * images : {"small":"https://img3.doubanio.com/view/subject/s/public/s1068520.jpg","large":"https://img3.doubanio.com/view/subject/l/public/s1068520.jpg","medium":"https://img3.doubanio.com/view/subject/m/public/s1068520.jpg"}
     * alt : https://book.douban.com/subject/1032501/
     * id : 1032501
     * publisher : 人民邮电出版社
     * isbn10 : 7115111308
     * isbn13 : 9787115111302
     * title : 心理学与生活
     * url : https://api.douban.com/v2/book/1032501
     * alt_title : Psychology and life
     * author_intro : 理查德·格里格（Richard J. Gerrig）是美国纽约州立大学的心理学教授。获Lex Hixon社会科学领域杰出教师奖。在认知心理学研究领域有专长，是美国心理学会实验心理学分会的会员。从《心理学与生活》这部经典教科书第14版修订时开始，格里格成为该书的合著者。
     菲利普·津巴多（Philip G. Zimbardo）是美国斯坦福大学的心理学教授，当代著名心理学家，美国心理学会主席。40多年来，由于他在心理学研究和教学领域的杰出贡献，美国心理学会特向津巴多频发了Hilgard普通心理学终生成就奖。由他开创的《心理学与生活》这部经典教科书哺育了一代又一代心理学工作者。津巴多主动让贤，推举格里格为《心理学与生活》第16版的第一作者。
     * summary : 《心理学与生活》是美国斯坦福大学多年来使用的教材，也是在美国许多大学里推广使用的经典教材，被ETS推荐为GRE心理学专项考试的主要参考用书，还是被许多国家大学的“普通心理学”课程选用的教材。这本教科书写作流畅，通俗易懂，深入生活，把心理学理论与知识联系人们的日常生活与工作，使它同样也成为一般大众了解心理学与自己的极好读物。
     作为一本包含着丰富的教育思想和独特教学方法的成熟教材，原书中所有元素——如由600余条词汇及解释组成的“专业术语表”，2000余条“参考文献”，以及近1000条的“人名和主题索引”等等，对于教学、研究和学习都十分宝贵，此中译本完整地翻译和保留了这些资料。
     * series : {"id":"31327","title":"新曲线·心理学丛书"}
     * price : 88.00元
     */

    private RatingBean rating;
    private String subtitle;
    private String pubdate;
    private String origin_title;
    private String image;
    private String binding;
    private String catalog;
    private String pages;
    private ImagesBean images;
    private String alt;
    private String id;
    private String publisher;
    private String isbn10;
    private String isbn13;
    private String title;
    private String url;
    private String alt_title;
    private String author_intro;
    private String summary;
    private SeriesBean series;
    private String price;
    private List<String> author;
    private List<TagsBean> tags;
    private List<String> translator;

    public RatingBean getRating() {
        return rating;
    }

    public void setRating(RatingBean rating) {
        this.rating = rating;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getOrigin_title() {
        return origin_title;
    }

    public void setOrigin_title(String origin_title) {
        this.origin_title = origin_title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public ImagesBean getImages() {
        return images;
    }

    public void setImages(ImagesBean images) {
        this.images = images;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlt_title() {
        return alt_title;
    }

    public void setAlt_title(String alt_title) {
        this.alt_title = alt_title;
    }

    public String getAuthor_intro() {
        return author_intro;
    }

    public void setAuthor_intro(String author_intro) {
        this.author_intro = author_intro;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public SeriesBean getSeries() {
        return series;
    }

    public void setSeries(SeriesBean series) {
        this.series = series;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<String> getAuthor() {
        return author;
    }

    public void setAuthor(List<String> author) {
        this.author = author;
    }

    public List<TagsBean> getTags() {
        return tags;
    }

    public void setTags(List<TagsBean> tags) {
        this.tags = tags;
    }

    public List<String> getTranslator() {
        return translator;
    }

    public void setTranslator(List<String> translator) {
        this.translator = translator;
    }

    public static class RatingBean {
        /**
         * max : 10
         * numRaters : 13032
         * average : 8.8
         * min : 0
         */

        private int max;
        private int numRaters;
        private String average;
        private int min;

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public int getNumRaters() {
            return numRaters;
        }

        public void setNumRaters(int numRaters) {
            this.numRaters = numRaters;
        }

        public String getAverage() {
            return average;
        }

        public void setAverage(String average) {
            this.average = average;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }
    }

    public static class ImagesBean {
        /**
         * small : https://img3.doubanio.com/view/subject/s/public/s1068520.jpg
         * large : https://img3.doubanio.com/view/subject/l/public/s1068520.jpg
         * medium : https://img3.doubanio.com/view/subject/m/public/s1068520.jpg
         */

        private String small;
        private String large;
        private String medium;

        public String getSmall() {
            return small;
        }

        public void setSmall(String small) {
            this.small = small;
        }

        public String getLarge() {
            return large;
        }

        public void setLarge(String large) {
            this.large = large;
        }

        public String getMedium() {
            return medium;
        }

        public void setMedium(String medium) {
            this.medium = medium;
        }
    }

    public static class SeriesBean {
        /**
         * id : 31327
         * title : 新曲线·心理学丛书
         */

        private String id;
        private String title;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public static class TagsBean {
        /**
         * count : 21991
         * name : 心理学
         * title : 心理学
         */

        private int count;
        private String name;
        private String title;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
