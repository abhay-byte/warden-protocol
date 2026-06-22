import os
from PIL import Image, ImageDraw, ImageFont, ImageFilter
import glob

# Ensure output directory exists
out_dir = "docs/storelisting"
os.makedirs(out_dir, exist_ok=True)

# Load resources
icon_path = "assets/icon.png"
bg_paths = glob.glob("assets/location_hero_sources/*.png") + glob.glob("assets/event_hero_sources/*.png")

try:
    font_large = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", 90)
    font_medium = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", 50)
except:
    font_large = ImageFont.load_default()
    font_medium = ImageFont.load_default()

screenshots = {
    1: {"file": "fastlane/metadata/android/en-US/images/1.png", "title": "WARDEN CORE HUB", "desc": "Monitor 1,000 survivors"},
    2: {"file": "fastlane/metadata/android/en-US/images/2.png", "title": "ARCHIVE LOGS", "desc": "Preserve cultural data"},
    3: {"file": "fastlane/metadata/android/en-US/images/3.png", "title": "LONG-RANGE CHRONICLE", "desc": "Generative AI epilogues"},
    4: {"file": "fastlane/metadata/android/en-US/images/4.png", "title": "SYSTEM CONFIG", "desc": "Manage critical subsystems"},
    5: {"file": "fastlane/metadata/android/en-US/images/5.png", "title": "SURFACE SCAN", "desc": "Explore post-apocalyptic sites"},
    6: {"file": "fastlane/metadata/android/en-US/images/6.png", "title": "CRITICAL EVENT", "desc": "Respond to apex threats"},
    7: {"file": "fastlane/metadata/android/en-US/images/7.png", "title": "PROTOCOL RESPONSE", "desc": "Take calculated risks"},
    8: {"file": "fastlane/metadata/android/en-US/images/8.png", "title": "COMMAND RESPONSE", "desc": "Live terminal directives"}
}

def create_promo_image(idx, data):
    # Base 1920x1080
    base = Image.new("RGB", (1920, 1080), (10, 15, 20))
    
    # Background
    bg_file = bg_paths[(idx - 1) % len(bg_paths)]
    bg = Image.open(bg_file).convert("RGBA")
    
    # Scale and crop background to fill 1920x1080
    bg_aspect = bg.width / bg.height
    base_aspect = 1920 / 1080
    if bg_aspect > base_aspect:
        # bg is wider
        new_w = int(bg_aspect * 1080)
        bg = bg.resize((new_w, 1080), Image.Resampling.LANCZOS)
        bg = bg.crop(((new_w - 1920)//2, 0, (new_w + 1920)//2, 1080))
    else:
        # bg is taller
        new_h = int(1920 / bg_aspect)
        bg = bg.resize((1920, new_h), Image.Resampling.LANCZOS)
        bg = bg.crop((0, (new_h - 1080)//2, 1920, (new_h + 1080)//2))
    
    # Blur and darken bg
    bg = bg.filter(ImageFilter.GaussianBlur(15))
    overlay = Image.new("RGBA", (1920, 1080), (0, 0, 0, 180))
    bg = Image.alpha_composite(bg, overlay)
    base.paste(bg, (0,0))
    
    # Draw text
    draw = ImageDraw.Draw(base)
    text_x = 150
    text_y = 450
    
    if idx == 1:
        # Add logo
        try:
            icon = Image.open(icon_path).convert("RGBA")
            icon = icon.resize((200, 200), Image.Resampling.LANCZOS)
            base.paste(icon, (text_x, text_y - 250), icon)
        except Exception as e:
            print("No icon:", e)

    draw.text((text_x, text_y), data["title"], fill=(255, 170, 0), font=font_large) # Amber color
    draw.text((text_x, text_y + 120), data["desc"], fill=(200, 200, 200), font=font_medium)
    
    # Screenshot
    try:
        ss = Image.open(data["file"]).convert("RGBA")
        # Resize to fit within 900 height
        ss_ratio = ss.width / ss.height
        new_h = 900
        new_w = int(new_h * ss_ratio)
        ss = ss.resize((new_w, new_h), Image.Resampling.LANCZOS)
        
        # Add border
        ss_with_border = Image.new("RGBA", (new_w + 10, new_h + 10), (100, 255, 100, 200)) # Green phosphor border
        ss_with_border.paste(ss, (5, 5))
        
        base.paste(ss_with_border, (1300 - new_w//2, 90), ss_with_border)
    except Exception as e:
        print("Missing ss:", e)
        
    base.save(f"{out_dir}/promo_{idx}.png")
    print(f"Created promo_{idx}.png")

for idx, data in screenshots.items():
    create_promo_image(idx, data)

# Feature Graphic (1024x500)
fg = Image.new("RGB", (1024, 500), (10, 15, 20))
try:
    bg_fg = Image.open(bg_paths[0]).convert("RGBA")
    bg_fg = bg_fg.resize((1024, int(1024 / (bg_fg.width/bg_fg.height))), Image.Resampling.LANCZOS)
    bg_fg = bg_fg.crop((0, (bg_fg.height - 500)//2, 1024, (bg_fg.height + 500)//2))
    fg.paste(bg_fg, (0,0))
    overlay = Image.new("RGBA", (1024, 500), (0, 0, 0, 150))
    fg.paste(overlay, (0,0), overlay)
except:
    pass

draw_fg = ImageDraw.Draw(fg)
try:
    font_fg_l = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", 60)
    font_fg_m = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", 30)
except:
    font_fg_l = ImageFont.load_default()
    font_fg_m = ImageFont.load_default()

try:
    icon = Image.open(icon_path).convert("RGBA")
    icon = icon.resize((120, 120), Image.Resampling.LANCZOS)
    fg.paste(icon, (80, 190), icon)
except:
    pass

draw_fg.text((230, 210), "THE WARDEN PROTOCOL", fill=(255, 170, 0), font=font_fg_l)
draw_fg.text((230, 280), "Post-Apocalyptic Vault Strategy", fill=(100, 255, 100), font=font_fg_m)
fg.save(f"{out_dir}/feature_graphic.png")

# Convert FG to 16:9 for GIF
fg_169 = Image.new("RGB", (1920, 1080), (0,0,0))
fg_resized = fg.resize((1920, int(1920 * (500/1024))), Image.Resampling.LANCZOS)
fg_169.paste(fg_resized, (0, (1080 - fg_resized.height)//2))
fg_169.save(f"{out_dir}/feature_graphic_169.png")

print("Done generating images.")
