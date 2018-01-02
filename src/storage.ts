import { v4 as newUuid } from 'uuid';
import { GameDto, VariantDescriptorDto, AppOptions, AppOptionsDto } from './dto';
import { VariantDescriptor, builtInVariants, Language } from './rules';


/**
 * Flag set if the Browser supports localStorage, false otherwise.
 */
export const isSupported: boolean = (() =>
{
    const testKey: string = '_civbuddy_dummy_';
    let readValue: string = '';
    try {
        window.localStorage.setItem(testKey, testKey);
        let readValue = window.localStorage.getItem(testKey);
        window.localStorage.removeItem(testKey);
        return testKey === readValue;
    } catch (e) {
        return false;
    }
})()


export enum StorageKeyType {
    GAME = 'CBG_',
    SITUATION = 'CBS_',
    VARIANT = 'CBV_',
    OPTIONS = 'CBO_'
}

export function newVariantKey(variantId: string): string {
    return StorageKeyType.VARIANT.toString() + variantId;
}

export function newGameKey(): string {
    return StorageKeyType.GAME.toString() + newUuid() + '_' + window.localStorage.length;
}

export function newSituationKey(): string {
    return StorageKeyType.SITUATION.toString() + newUuid() + '_' + window.localStorage.length;
}

const appOptionsKey: string = StorageKeyType.OPTIONS + 'Settings';


function hideFields(...pFieldsToHide: string[]): (pKey: string, pValue: any) => any
{
    return function(pKey: string, pValue: any)
    {
        if (pFieldsToHide.indexOf(pKey) >= 0) {
            return undefined;
        }
        return pValue;
    }
}

function getJsonElement(pElementName: string, pJson: Object): string {
    let result: string = '';
    if (pJson.hasOwnProperty(pElementName)) {
        result = pJson[pElementName];
    }
    return result;
}

function parseQuietly(pContent: string): Object {
    let json: Object = {};
    try {
        json = JSON.parse(pContent);
    } catch (e) {
        // ignore
    }
    return json;
}


/* ================================================================================================================
 *     GAMES
 * ============================================================================================================= */

export function readListOfGames(): GameDto[] {
    const ls: Storage = window.localStorage;
    let result: GameDto[] = [];
    for (let i = 0; i < ls.length; ++i) {
        let key: string | null = ls.key(i);
        if (key !== null && key.startsWith(StorageKeyType.GAME.toString())) {
            let value: string | null = ls.getItem(key);
            if (value !== null) {
                let game: GameDto = <GameDto>JSON.parse(value);
                game.key = key;
                result.push(game);
            }
        }
    }
    return result;
}

export function deleteGame(pGameKey: string): void {
    const ls: Storage = window.localStorage;
    ls.removeItem(pGameKey);
}

export function createGame(pGameDto: GameDto): void {
    const ls: Storage = window.localStorage;
    ls.setItem(pGameDto.key, JSON.stringify(pGameDto, hideFields("key")));
}


/* ================================================================================================================
 *     VARIANTS
 * ============================================================================================================= */

let variants: VariantDescriptor[] = (() =>
{
    const ls: Storage = window.localStorage;
    let result: VariantDescriptor[] = [];
    for (let i = 0; i < ls.length; ++i) {
        let key: string | null = ls.key(i);
        if (key !== null && key.startsWith(StorageKeyType.VARIANT.toString())) {
            let value: string | null = ls.getItem(key);
            if (value !== null) {
                const json: Object = parseQuietly(value);
                const variantId: string = getJsonElement('variantId', json);
                if (variantId.length > 0) {
                    result.push(new VariantDescriptorDto(key, variantId));
                }
            }
        }
    }
    return result;
})();
export default variants;



export function ensureBuiltInVariants(): void {
    for(let variantId in builtInVariants) {
        const variantKey: string = newVariantKey(variantId);
        let currentContent: string | null = window.localStorage.getItem(variantKey);
        if (currentContent === null || currentContent.length === 0) {
            window.localStorage.setItem(variantKey, JSON.stringify(builtInVariants[variantId]));
            console.log('Variant \'' + variantId + '\' stored in localStorage as \'' + variantKey + '\'');
        }
     }
}


/* ================================================================================================================
 *     GLOBAL APPLICATION OPTIONS
 * ============================================================================================================= */

export function readOptions(): AppOptions {
    const ls: Storage = window.localStorage;
    let result: AppOptions = buildDefaultOptions();
    let value: string | null = ls.getItem(appOptionsKey);
    if (value !== null) {
        const json: Object = parseQuietly(value);
        const languageStr: string = getJsonElement('language', json);
        const langEnum: Language = Language[languageStr.toUpperCase()];
        if (languageStr.length > 0 && typeof(langEnum) !== 'undefined') {
            result = new AppOptionsDto(langEnum);
        }
    }
    console.log("Read application options: " + JSON.stringify(result));
    return result;
}

function buildDefaultOptions(): AppOptions {
    return new AppOptionsDto(Language.EN);
}

export function writeOptions(pAppOptions: AppOptions): void {
    const ls: Storage = window.localStorage;
    const value: string = JSON.stringify(pAppOptions);
    ls.setItem(appOptionsKey, value);
    console.log('Global application options stored in localStorage as \'' + appOptionsKey + '\' = ' + value);
}
