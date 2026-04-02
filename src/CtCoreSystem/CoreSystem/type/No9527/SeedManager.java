package CtCoreSystem.CoreSystem.type.No9527;

import arc.Core;
import arc.files.Fi;
import arc.math.Mathf;
import arc.util.Log;

import static CtCoreSystem.CtCoreSystem.DEFAULT_BASE_SEED;
import static CtCoreSystem.CtCoreSystem.DEFAULT_SEED;

/**
 * 星球种子设置 1/2
 * 种子管理器 - 负责读写和管理星球生成种子
 */
public class SeedManager {
    // 种子数据文件路径
    public static final Fi SEED_FILE = Core.settings.getDataDirectory().child("mods/creatorsdlc/ctseed.dat");



    // 静态初始化块 - 确保文件存在
    static {
        initializeSeedFile();
    }

    /**
     * 初始化种子文件 - 确保文件存在
     */
    private static void initializeSeedFile() {
        try {
            // 确保目录存在
            SEED_FILE.parent().mkdirs();

            // 如果文件不存在，创建一个包含默认值的文件
            if (!SEED_FILE.exists()) {
                String defaultContent = DEFAULT_SEED + "," + DEFAULT_BASE_SEED;
                SEED_FILE.writeString(defaultContent);
                Log.info("已创建种子文件，使用默认值: " + defaultContent);
            }
        } catch (Exception e) {
            Log.err("初始化种子文件失败: " + e.getMessage());
        }
    }

    /**
     * 种子数据结构
     */
    public static class SeedData {
        public int seed;
        public int baseSeed;

        public SeedData(int seed, int baseSeed) {
            this.seed = seed;
            this.baseSeed = baseSeed;
        }

        public SeedData() {
            this(DEFAULT_SEED, DEFAULT_BASE_SEED);
        }
    }

    /**
     * 保存种子到文件
     */
    public static void saveSeeds(int seed, int baseSeed) {
        try {
            // 确保目录存在
            SEED_FILE.parent().mkdirs();

            // 直接以 "seed,baseSeed" 格式保存
            String content = seed + "," + baseSeed;
            SEED_FILE.writeString(content);

            Log.info("种子已保存: seed=" + seed + ", baseSeed=" + baseSeed);
        } catch (Exception e) {
            Log.err("保存种子失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 从文件读取种子
     */
    public static SeedData loadSeeds() {
        try {
            if (!SEED_FILE.exists()) {
                Log.info("种子文件不存在，使用默认值");
                return new SeedData(DEFAULT_SEED, DEFAULT_BASE_SEED);
            }

            // 读取文件内容
            String content = SEED_FILE.readString().trim();

            // 解析格式 "seed,baseSeed"
            String[] parts = content.split(",");
            if (parts.length != 2) {
                throw new IllegalArgumentException("种子文件格式错误");
            }

            int seed = Integer.parseInt(parts[0].trim());
            int baseSeed = Integer.parseInt(parts[1].trim());

            SeedData data = new SeedData(seed, baseSeed);
            Log.info("种子已加载: seed=" + data.seed + ", baseSeed=" + data.baseSeed);
            return data;
        } catch (Exception e) {
            Log.err("读取种子失败，使用默认值: " + e.getMessage());
            return new SeedData(DEFAULT_SEED, DEFAULT_BASE_SEED);
        }
    }

    /**
     * 检查种子文件是否存在
     */
    public static boolean seedFileExists() {
        return SEED_FILE.exists();
    }

    /**
     * 删除种子文件
     */
    public static void deleteSeedFile() {
        if (SEED_FILE.exists()) {
            SEED_FILE.delete();
            Log.info("种子文件已删除");
        }
    }

    /**
     * 验证种子值是否有效
     */
    public static boolean isValidSeed(int seed) {
        return seed >= 0 && seed <= 999999; // 限制种子范围
    }
}
